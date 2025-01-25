//
//  AppStateViewModel.swift
//  Hacker News
//
//  Created by Trevor Elkins on 6/20/23.
//

import Foundation
import SwiftData
import SwiftUI

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

extension Bookmark {
  func toStoryContent() -> StoryContent {
    return StoryContent(
      id: uid,
      title: title,
      author: by,
      body: text,
      score: score,
      commentCount: descendants,
      timestamp: time,
      url: url,
      bookmarked: true
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
    case .loading(let id):
      return id
    case .loaded(let content):
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

@Observable @MainActor
final class AppViewModel {

  enum AppNavigation: Codable, Hashable {
    case webLink(url: URL, title: String)
    case storyComments(story: Story)
  }

  var authState: AuthState
  var showLoginSheet: Bool = false
  var feedState = FeedState()
  var navigationPath = NavigationPath()
  var bookmarks: [Bookmark]

  private let bookmarkStore: BookmarksDataStore
  private let api = HNApi()
  private let webClient = HNWebClient()

  private var pager = Pager()
  private let cookieStorage = HTTPCookieStorage.shared
  @ObservationIgnored private var loadingTask: Task<Void, Never>?

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
    feedState.setStories([], for: feedType)

    let idsToConsume = await api.fetchStories(feedType: feedType)
    pager.setIds(idsToConsume)

    if pager.hasNextPage() {
      let nextPage = pager.nextPage()
      let loadingStates = nextPage.ids.map { StoryState.loading(id: $0) }
      feedState.setStories(loadingStates, for: feedType)

      let items = await api.fetchPage(page: nextPage)
      var newStories = items.map { story in
        let bookmarked = bookmarkStore.containsBookmark(with: story.id)
        return StoryState.loaded(content: story.toStoryContent(bookmarked: bookmarked))
      }
      if pager.hasNextPage() {
        newStories.append(.nextPage)
      }
      feedState.setStories(newStories, for: feedType)
    }
  }

  func fetchNextPage() async {
    guard pager.hasNextPage() else {
      return
    }
    let nextPage = pager.nextPage()
    let items = await api.fetchPage(page: nextPage)

    var currentStories = feedState.storiesForFeed(feedState.selectedFeed)
    if !currentStories.isEmpty {
      currentStories.removeLast()  // remove the loading view
    }

    var newStories =
      currentStories
      + items.map { story in
        let bookmarked = bookmarkStore.containsBookmark(with: story.id)
        return .loaded(content: story.toStoryContent(bookmarked: bookmarked))
      }
    if pager.hasNextPage() {
      newStories.append(.nextPage)
    }
    feedState.setStories(newStories, for: feedState.selectedFeed)
  }

  func fetchBookmarks() {
    bookmarks = bookmarkStore.fetchBookmarks()
  }

  private func isLoggedIn() -> Bool {
    return cookieStorage.cookies?.isEmpty == false
  }

  func backPressed() {
    navigationPath.removeLast()
  }

  func gotoLogin() {
    if authState == .loggedOut {
      showLoginSheet = true
    } else {
      cookieStorage.removeCookies()
      authState = .loggedOut
    }
  }

  func openLink(url: URL) {
    navigationPath.append(AppNavigation.webLink(url: url, title: ""))
  }

  func toggleBookmark(_ item: StoryContent) {
    let currentStories = feedState.storiesForFeed(feedState.selectedFeed)
    let updatedStories = currentStories.map { current in
      if case .loaded(let content) = current {
        if content.id == item.id {
          return StoryState.loaded(content: item)
        }
      }
      return current
    }
    feedState.setStories(updatedStories, for: feedState.selectedFeed)

    if item.bookmarked {
      bookmarkStore.addBookmark(item.toBookmark())
      bookmarks = bookmarkStore.fetchBookmarks()
    } else {
      bookmarkStore.removeBookmark(with: item.id)
      bookmarks = bookmarkStore.fetchBookmarks()
    }
  }

  func loginSubmit(username: String, password: String) async {
    let status = await webClient.login(acct: username, pw: password)
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
