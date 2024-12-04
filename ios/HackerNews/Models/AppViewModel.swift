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

enum StoriesState {
  case notStarted
  case loading
  case loaded(items: [Story])
}

struct PostListState {
  var feeds: [FeedType] = FeedType.allCases
  var storiesState: StoriesState = .notStarted
  var selectedFeed: FeedType = FeedType.top
}

@MainActor
class AppViewModel: ObservableObject {
  
  enum AppNavigation: Codable, Hashable {
    case webLink(url: URL, title: String)
    case storyComments(story: Story)
  }
  
  enum AuthState {
    case loggedIn
    case loggedOut
  }
  
  
  @Published var authState = AuthState.loggedOut
  @Published var postListState = PostListState()
  @Published var navigationPath = NavigationPath()
  
  private let hnApi = HNApi()
  
  init() {}
  
  func performLogin() {
    authState = .loggedIn
  }
  
  func performLogout() {
    authState = .loggedOut
  }
  
  func fetchPosts(feedType: FeedType) async {
    var updated = postListState
    updated.selectedFeed = feedType
    updated.storiesState = .loading
    postListState = updated
    
    let stories = await hnApi.fetchStories(feedType: feedType)
    updated.storiesState = .loaded(items: stories)
    postListState = updated
  }
}
