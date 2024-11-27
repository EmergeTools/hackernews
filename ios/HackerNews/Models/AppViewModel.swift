//
//  AppStateViewModel.swift
//  Hacker News
//
//  Created by Trevor Elkins on 6/20/23.
//

import Foundation
import SwiftUI

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
  
  enum FeedType: CaseIterable {
    case top
    case new
    
    var title: String {
      switch self {
      case .top:
        return "Top"
      case .new:
        return "New"
      }
    }
  }
  
  struct StoriesState {
    var stories: [Story]
    var feedType: FeedType = .top
  }
  
  enum StoriesListState {
    case notStarted
    case loading
    case loaded(state: StoriesState)
  }
  
  @Published var authState = AuthState.loggedOut
  @Published var storiesState = StoriesListState.notStarted
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
    storiesState = .loading
    let stories = switch feedType {
    case .top:
      await hnApi.fetchTopStories()
    case .new:
      await hnApi.fetchNewStories()
    }
    storiesState = .loaded(state: StoriesState(stories: stories, feedType: feedType))
  }
  
}
