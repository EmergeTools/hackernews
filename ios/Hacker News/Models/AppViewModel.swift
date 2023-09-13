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
  
  enum StoriesListState {
    case notStarted
    case loading
    case loaded(stories: [Story])
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
  
  func fetchPosts() async {
    storiesState = .loading
    let stories = await hnApi.fetchTopStories()
    storiesState = .loaded(stories: stories)
  }
  
}
