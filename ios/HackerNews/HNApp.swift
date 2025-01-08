//
//  Hacker_NewsApp.swift
//  Hacker News
//
//  Created by Trevor Elkins on 6/20/23.
//

import Reaper
import Sentry
import SwiftUI

@main
struct Hacker_NewsApp: App {

  @StateObject private var appState = AppViewModel()

  init() {
    UINavigationBar.appearance().backgroundColor = .clear
    UICollectionView.appearance().backgroundColor = .clear
    
    EMGReaper.sharedInstance().start(withAPIKey: "f77fb081-cfc2-4d15-acb5-18bad59c9376")
    
    SentrySDK.start { options in
        options.dsn = "https://118cff4b239bd3e0ede8fd74aad9bf8f@o497846.ingest.sentry.io/4506027753668608"
#if DEBUG
        options.debug = true
#else
        options.debug = false
#endif
        options.enableTracing = true
    }
  }

  var body: some Scene {
    WindowGroup {
      NavigationStack(path: $appState.navigationPath) {
        ZStack {
          HNColors.background
            .ignoresSafeArea()

          ContentView(model: appState)
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
