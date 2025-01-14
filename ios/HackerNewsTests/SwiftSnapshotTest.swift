//
//  SwiftSnapshotTest.swift
//  HackerNews
//
//  Created by Trevor Elkins on 11/19/24.
//

import SnapshotTesting
import SwiftUI
import XCTest

@testable import HackerNews

final class SwiftSnapshotTest: XCTestCase {

  var appViewModel: AppViewModel!

  @MainActor override func setUp() {
    super.setUp()
    appViewModel = AppViewModel()
  }

  override func tearDown() {
    appViewModel = nil
    super.tearDown()
  }

  override func invokeTest() {
    // Always record new snapshots
    withSnapshotTesting(record: .all) {
      super.invokeTest()
    }
  }

  @MainActor func testPostListScreen() {
    // Test default state
    let defaultView = PostListScreen(model: appViewModel)

    // Test loading state
    let loadingViewModel = AppViewModel()
    loadingViewModel.postListState = PostListState(
      stories: []
    )
    let loadingView = PostListScreen(model: loadingViewModel)

    // Test loaded state with posts
    let loadedViewModel = AppViewModel()
    loadedViewModel.postListState = PostListState(
      stories: PreviewHelpers.makeFakeStories().map { StoryState.loaded(story: $0) }
    )
    let loadedView = PostListScreen(model: loadedViewModel)

    let devices = [
      ("iPhone SE", ViewImageConfig.iPhoneSe),
      ("iPhone 12", ViewImageConfig.iPhone12),
      ("iPhone 13 Pro", ViewImageConfig.iPhone13Pro),
    ]

    // Test each state on different devices
    for (name, config) in devices {
      // Default state
      assertSnapshot(
        of: defaultView.toVC(),
        as: .image(on: config),
        named: "PostListScreen-Default-\(name)"
      )
      assertSnapshot(
        of: defaultView.toVC(),
        as: .image(on: config, traits: .init(userInterfaceStyle: .dark)),
        named: "PostListScreen-Default-\(name)-DarkMode"
      )

      // Loading state
      assertSnapshot(
        of: loadingView.toVC(),
        as: .image(on: config),
        named: "PostListScreen-Loading-\(name)"
      )
      assertSnapshot(
        of: loadingView.toVC(),
        as: .image(on: config, traits: .init(userInterfaceStyle: .dark)),
        named: "PostListScreen-Loading-\(name)-DarkMode"
      )

      // Loaded state with posts
      assertSnapshot(
        of: loadedView.toVC(),
        as: .image(on: config),
        named: "PostListScreen-LoadedPosts-\(name)"
      )
      assertSnapshot(
        of: loadedView.toVC(),
        as: .image(on: config, traits: .init(userInterfaceStyle: .dark)),
        named: "PostListScreen-LoadedPosts-\(name)-DarkMode"
      )
    }
  }
}

extension View {
  fileprivate func toVC() -> UIViewController {
    let hostingController = UIHostingController(rootView: self)
    hostingController.view.frame = UIScreen.main.bounds
    return hostingController
  }
}
