//
//  AppStateViewModel.swift
//  Hacker News
//
//  Created by Trevor Elkins on 6/20/23.
//

import Foundation
import SwiftUI
import SwiftData

enum FeedType: CaseIterable {
  case top
  case new
  case best
  case ask
  case show

  var title: String {
    switch self {
    case .top:
      return "Top"
    case .new:
      return "New"
    case .best:
      return "Best"
    case .ask:
      return "Ask"
    case .show:
      return "Show"
    }
  }
}

struct FeedState {
  var feeds: [FeedType] = FeedType.allCases
  var selectedFeed: FeedType = FeedType.top
  var stories: [StoryState] = []
}

struct StoryContent {
  var id: Int64
  var title: String
  var author: String?
  var body: String?
  var score: Int
  var commentCount: Int
  var timestamp: Int64
  var url: String?
  var bookmarked: Bool = false

  func relativeDate() -> String {
    let date = Date(timeIntervalSince1970: TimeInterval(timestamp))
    return date.timeAgoDisplay()
  }

  func makeUrl() -> URL? {
    guard let url = url else {
      return nil
    }
    return URL(string: url)
  }
}

extension Story {
  func toStoryContent(bookmarked: Bool = false) -> StoryContent {
    return StoryContent(
      id: id,
      title: title,
      author: by,
      body: text,
      score: score,
      commentCount: descendants,
      timestamp: time,
      url: url,
      bookmarked: bookmarked
    )
  }
}

extension StoryContent {
  func toStory() -> Story {
    return Story(
      id: id,
      by: author,
      time: timestamp,
      type: .story,
      title: title,
      text: body,
      url: url,
      score: score,
      descendants: commentCount,
      kids: []
    )
  }

}

enum StoryState: Identifiable {
  case loading(id: Int64)
  case loaded(content: StoryContent)
  case nextPage

  var id: Int64 {
    switch self {
    case .loading(id: let id):
      return id
    case .loaded(content: let content):
      return content.id
    case .nextPage:
      return Int64.max
    }
  }
}

enum AuthState {
  case loggedIn
  case loggedOut
}

@MainActor
class AppViewModel: ObservableObject {

  enum AppNavigation: Codable, Hashable {
    case webLink(url: URL, title: String)
    case storyComments(story: Story)
  }

  @Published var authState: AuthState
  @Published var showLoginSheet: Bool = false
  @Published var feedState = FeedState()
  @Published var navigationPath = NavigationPath()

  private let bookmarkStore: BookmarksDataStore
  private let api = HNApi()
  private let webClient = HNWebClient()
  private let bookmarks: [Bookmark]

  private var pager = Pager()
  private let cookieStorage = HTTPCookieStorage.shared
  private var loadingTask: Task<Void, Never>?

  init(bookmarkStore: BookmarksDataStore) {
    self.bookmarkStore = bookmarkStore
    self.bookmarks = bookmarkStore.fetchBookmarks()
    authState = self.cookieStorage.cookies?.isEmpty == true ? .loggedOut : .loggedIn
    loadingTask = Task {
      await fetchInitialPosts(feedType: .top)
    }
  }

  deinit {
    loadingTask?.cancel()
  }

  func fetchInitialPosts(feedType: FeedType) async {
    feedState.selectedFeed = feedType
    feedState.stories = []

    let idsToConsume = await api.fetchStories(feedType: feedType)
    pager.setIds(idsToConsume)

    if pager.hasNextPage() {
      let nextPage = pager.nextPage()
      feedState.stories = nextPage.ids.map { StoryState.loading(id: $0) }

      let items = await api.fetchPage(page: nextPage)
      feedState.stories = items.map { story in
        let bookmarked = self.bookmarks.contains(where: { $0.id == story.id })
        return .loaded(content: story.toStoryContent())
      }
      pager.hasNextPage() ? feedState.stories.append(.nextPage) : ()
    }
  }

  func fetchNextPage() async {
    guard pager.hasNextPage() else {
      return
    }
    let nextPage = pager.nextPage()
    let items = await api.fetchPage(page: nextPage)
    feedState.stories.removeLast() // remove the loading view
    feedState.stories += items.map { story in
      let bookmarked = self.bookmarks.contains(where: { $0.id == story.id })
      return .loaded(content: story.toStoryContent())
    }
    pager.hasNextPage() ? feedState.stories.append(.nextPage) : ()
  }


  private func isLoggedIn() -> Bool {
    return cookieStorage.cookies?.isEmpty == false
  }

  func backPressed() {
    navigationPath.removeLast()
  }

  func gotoLogin() {
    if (authState == .loggedOut) {
      showLoginSheet = true
    } else {
      cookieStorage.removeCookies()
      authState = .loggedOut
    }
  }

  func toggleBookmark(_ item: StoryContent) {
    feedState.stories = feedState.stories.map { current in
      if case .loaded(let content) = current {
        if content.id == item.id {
          return .loaded(content: item)
        } else {
          return current
        }
      } else {
        return current
      }
    }

    if item.bookmarked {
      bookmarkStore.addBookmark(item.toBookmark())
    } else {
      bookmarkStore.removeBookmark(with: item.id)
    }
  }

  func loginTapped(username: String, password: String) async {
    let status = await webClient.login(acct: username, pw: password   )
    print("Login Status: \(status)")
    switch status {
    case .success:
      showLoginSheet = false
      authState = .loggedIn
    case .error:
      print("Login failed")
      authState = .loggedOut
    }
  }
}
