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
  
  @Published var isLoadingPosts = false
  @Published var stories: [Story] = []
  
  @Published var authState = AuthState.loggedOut
  
  private let hnApi = HNApi()
  
  init() {}
  
  func performLogin() {
    authState = .loggedIn
  }
  
  func performLogout() {
    authState = .loggedOut
  }
  
  func fetchPosts() async {
    isLoadingPosts = true
    stories = await hnApi.fetchTopStories()
    isLoadingPosts = false
  }
  
}
