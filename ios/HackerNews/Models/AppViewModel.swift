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
  
  var id: Int64 {
    switch self {
    case .loading(id: let id):
      return id
    case .loaded(story: let story):
      return story.id
    }
  }
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
    updated.stories = []
    postListState = updated
    
    let storyIds = await hnApi.fetchStories(feedType: feedType)
    print("Loading Feed: \(feedType) with ids \(storyIds)")
  }
}
