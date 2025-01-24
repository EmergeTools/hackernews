//
//  Hacker_NewsApp.swift
//  Hacker News
//
//  Created by Trevor Elkins on 6/20/23.
//

import Reaper
import Sentry
import SwiftUI
import SwiftData

@main
struct Hacker_NewsApp: App {
  @State private var appModel = AppViewModel(bookmarkStore: LiveBookmarksDataStore.shared)

  init() {
    UINavigationBar.appearance().backgroundColor = .clear
    UICollectionView.appearance().backgroundColor = .clear
    
    EMGReaper.sharedInstance().start(withAPIKey: "f77fb081-cfc2-4d15-acb5-18bad59c9376")
    
    SentrySDK.start { options in
        options.dsn = "https://118cff4b239bd3e0ede8fd74aad9bf8f@o497846.ingest.sentry.io/4506027753668608"
        options.enableTracing = true
    }
  }

  var body: some Scene {
    WindowGroup {
      NavigationStack(path: $appModel.navigationPath) {
        ZStack {
          HNColors.background
            .ignoresSafeArea()

          ContentView(model: $appModel)
        }
        .navigationDestination(for: AppViewModel.AppNavigation.self) { appNavigation in
          switch appNavigation {
          case .webLink(let url, let title):
            WebView(url: url)
              .ignoresSafeArea()
              .navigationTitle(title)
              .navigationBarTitleDisplayMode(.inline)
          case .storyComments(let story):
            let commentModel = CommentsViewModel(story: story, auth: appModel.authState) { destination in
              switch destination {
              case .back:
                appModel.backPressed()
              case .login:
                appModel.gotoLogin()
              }
            }
            CommentsScreen(model: commentModel)
              .navigationBarBackButtonHidden()
          }
        }
        .sheet(isPresented: $appModel.showLoginSheet) {
          LoginScreen(model: $appModel)
            .presentationDetents([.medium])
            .presentationCornerRadius(24)
        }
      }
    }
  }
}
