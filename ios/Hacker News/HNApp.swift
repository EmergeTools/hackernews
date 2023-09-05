//
//  Hacker_NewsApp.swift
//  Hacker News
//
//  Created by Trevor Elkins on 6/20/23.
//

import SwiftUI

@main
struct Hacker_NewsApp: App {
  
  @StateObject private var appState = AppViewModel()
  
  init() {
    UINavigationBar.appearance().backgroundColor = .clear
    UICollectionView.appearance().backgroundColor = .clear
  }
  
  var body: some Scene {
    WindowGroup {
      NavigationStack(path: $appState.navigationPath) {
        ZStack {
          HNColors.background
            .ignoresSafeArea()
          
          ContentView(appState: appState)
        }
        .toolbarColorScheme(.dark, for: .navigationBar)
        .toolbarBackground(HNColors.orange, for: .navigationBar)
        .toolbarBackground(.visible, for: .navigationBar)
        .navigationDestination(for: AppViewModel.AppNavigation.self) { appNavigation in
          switch appNavigation {
          case .webLink(let url, let title):
            WebView(url: url)
              .ignoresSafeArea()
              .navigationTitle(title)
              .navigationBarTitleDisplayMode(.inline)
          case .storyComments(let story):
            let model = StoryViewModel(story: story)
            StoryScreen(storyModel: model)
              .background(.clear)
              .task {
                await model.fetchComments()
              }
          }
        }
      }
    }
  }
}
