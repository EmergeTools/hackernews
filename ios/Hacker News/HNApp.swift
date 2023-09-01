//
//  Hacker_NewsApp.swift
//  Hacker News
//
//  Created by Trevor Elkins on 6/20/23.
//

import SwiftUI

@main
struct Hacker_NewsApp: App {
  
  enum AppNavigation: Codable, Hashable {
    case webLink(url: URL, title: String)
    case storyComments(story: Story)
  }
  
  @StateObject private var appState = AppViewModel()
  @State private var path = NavigationPath()
  
  init() {
    UINavigationBar.appearance().backgroundColor = .clear
  }
  
  var body: some Scene {
    WindowGroup {
      NavigationStack(path: $path) {
        ZStack {
          HNColors.background
            .ignoresSafeArea()
          
          ContentView(appState: appState)
        }
        .toolbarColorScheme(.dark, for: .navigationBar)
        .toolbarBackground(HNColors.orange, for: .navigationBar)
        .toolbarBackground(.visible, for: .navigationBar)
        .navigationDestination(for: AppNavigation.self) { appNavigation in
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
                print("Fetching comments")
                await model.fetchComments()
              }
          }
        }
      }
    }
  }
}
