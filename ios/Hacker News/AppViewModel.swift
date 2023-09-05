//
//  AppStateViewModel.swift
//  Hacker News
//
//  Created by Trevor Elkins on 6/20/23.
//

import Foundation

@MainActor
class AppViewModel: ObservableObject {
  
  enum AuthState {
    case loggedIn
    case loggedOut
  }
  
  enum StoriesListState {
    case notStarted
    case loading
    case loaded(stories: [Story])
  }
  
  @Published var authState = AuthState.loggedOut
  @Published var storiesState = StoriesListState.notStarted
  
  private let hnApi = HNApi()
  
  init() {}
  
  func performLogin() {
    authState = .loggedIn
  }
  
  func performLogout() {
    authState = .loggedOut
  }
  
  func fetchPosts() async {
    storiesState = .loading
    let stories = await hnApi.fetchTopStories()
    storiesState = .loaded(stories: stories)
  }
  
}
