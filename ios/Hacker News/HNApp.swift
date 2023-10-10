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

  @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate
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

class AppDelegate: NSObject, UIApplicationDelegate {
  func application(
    _ application: UIApplication, didFinishLaunchingWithOptions
    launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool
  {
    EMGReaper.sharedInstance().start(withAPIKey: "f77fb081-cfc2-4d15-acb5-18bad59c9376")
    
    SentrySDK.start { options in
        options.dsn = "https://118cff4b239bd3e0ede8fd74aad9bf8f@o497846.ingest.sentry.io/4506027753668608"
        options.debug = true // Enabled debug when first installing is always helpful
        options.enableTracing = true

        // Uncomment the following lines to add more data to your events
        // options.attachScreenshot = true // This adds a screenshot to the error events
        // options.attachViewHierarchy = true // This adds the view hierarchy to the error events
    }
    
    return true
  }
}
