//
//  AppStateViewModel.swift
//  Hacker News
//
//  Created by Trevor Elkins on 6/20/23.
//

import Foundation
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

/**
 Display State
 needs information for the feed selection
 needs a list of Stories for the feed
 the feed items can have their own loading state
 */
struct PostListState {
  var feeds: [FeedType] = FeedType.allCases
  var selectedFeed: FeedType = FeedType.top
  var stories: [StoryState] = []
}

enum StoryState: Identifiable {
  case loading(id: Int64)
  case loaded(story: Story)
  case nextPage

  var id: Int64 {
    switch self {
    case .loading(id: let id):
      return id
    case .loaded(story: let story):
      return story.id
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

  @Published var showLoginSheet: Bool
  @Published var authState: AuthState = .loggedOut
  @Published var postListState = PostListState()
  @Published var navigationPath = NavigationPath()

  private let api = HNApi()
  private let webClient = HNWebClient()
  private var pager = Pager()
  private let cookieStorage = HTTPCookieStorage.shared

  init(showLogin: Bool = false) {
    self.showLoginSheet = showLogin
  }

  func fetchInitialPosts(feedType: FeedType) async {
    postListState.selectedFeed = feedType
    postListState.stories = []

    let idsToConsume = await api.fetchStories(feedType: feedType)
    pager.setIds(idsToConsume)

    if pager.hasNextPage() {
      let nextPage = pager.nextPage()
      postListState.stories = nextPage.ids.map { StoryState.loading(id: $0) }

      let items = await api.fetchPage(page: nextPage)
      postListState.stories = items.map { StoryState.loaded(story: $0 ) }
      pager.hasNextPage() ? postListState.stories.append(.nextPage) : ()
    }
  }

  func fetchNextPage() async {
    guard pager.hasNextPage() else {
      return
    }
    let nextPage = pager.nextPage()
    let items = await api.fetchPage(page: nextPage)
    postListState.stories.removeLast() // remove the loading view
    postListState.stories += items.map { StoryState.loaded(story: $0) }
    pager.hasNextPage() ? postListState.stories.append(.nextPage) : ()
  }

  private func isLoggedIn() -> Bool {
    return cookieStorage.cookies?.isEmpty == false
  }

  func loginRowTapped() {
    if (authState == .loggedOut) {
      showLoginSheet = true
    } else {
      authState = .loggedOut
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
