//
//  Hacker_NewsApp.swift
//  Hacker News
//
//  Created by Trevor Elkins on 6/20/23.
//

import Reaper
import Sentry
import SwiftData
import SwiftUI
import Common

@main
struct HackerNewsApp: App {
  @State private var appModel = AppViewModel(
    bookmarkStore: LiveBookmarksDataStore.shared)
  @State private var theme = Theme()

  init() {
    UINavigationBar.appearance().backgroundColor = .clear
    UICollectionView.appearance().backgroundColor = .clear

    EMGReaper.sharedInstance().start(
      withAPIKey: "f77fb081-cfc2-4d15-acb5-18bad59c9376")

    SentrySDK.start { options in
      options.dsn =
      "https://118cff4b239bd3e0ede8fd74aad9bf8f@o497846.ingest.sentry.io/4506027753668608"
      
      options.configureUserFeedback = { config in
        config.onSubmitSuccess = { data in
          print("Feedback submitted successfully: \(data)")
        }
        config.onSubmitError = { error in
          print("Failed to submit feedback: \(error)")
        }
      }
      
      options.enableAppHangTrackingV2 = true
      options.sessionReplay.onErrorSampleRate = 1.0
      options.sendDefaultPii = true
      
#if DEBUG
      options.environment = "development"
      options.sessionReplay.sessionSampleRate = 1.0
      options.tracesSampleRate = 1
//      options.debug = true
      options.configureProfiling = {
        $0.profileAppStarts = true
        $0.lifecycle = .trace
        $0.sessionSampleRate = 1.0
      }
#else
      options.environment = "production"
      options.sessionReplay.sessionSampleRate = 0.1
      options.tracesSampleRate = 0.1
      options.debug = false
      options.configureProfiling = {
        $0.profileAppStarts = true
        $0.lifecycle = .trace
        $0.sessionSampleRate = 0.1
      }
#endif
      
      if NSClassFromString("XCTest") != nil {
        options.environment = "xctest"
      }
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
        .navigationDestination(for: AppViewModel.AppNavigation.self) {
          appNavigation in
          switch appNavigation {
          case .webLink(let url, let title):
            WebViewContainer(url: url, title: title)
          case .storyComments(let story):
            let commentModel = CommentsViewModel(
              story: story, auth: appModel.authState
            ) {
              destination in
              switch destination {
              case .back:
                appModel.backPressed()
              case .login:
                appModel.gotoLogin()
              case let .website(url):
                appModel.openLink(url: url)
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
      .environment(theme)
      .onOpenURL { url in
        handleDeepLink(url)
      }
#if ADHOC
      .onAppear() {
        AutoUpdateManager.checkForUpdates()
      }
#endif
    }
  }

  private func handleDeepLink(_ url: URL) {
    guard url.scheme == "hackernews",
      url.host == "story",
      let storyId = Int64(url.lastPathComponent)
    else {
      return
    }

    Task {
      let stories = await HNApi().fetchPage(page: Page(ids: [storyId]))
      if let story = stories.first {
        appModel.navigationPath
          .append(AppViewModel.AppNavigation.storyComments(story: story))
      }
    }
  }
}
