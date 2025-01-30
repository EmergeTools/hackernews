//
//  Previews.swift
//  HackerNews
//
//  Created by Trevor Elkins on 6/17/24.
//

import Foundation
import SnapshotPreferences
import SwiftUI
import HackerNewsCommon

struct PreviewVariants<Content: View>: View {
  let content: Content

  init(@ViewBuilder _ content: () -> Content) {
    self.content = content()
  }

  var body: some View {
    Group {
      self.content
        .environment(\.colorScheme, .light)
        .preferredColorScheme(.light)
        .navigationBarHidden(true)
        .previewDevice("iPhone 11 Pro Max")
        .previewDisplayName("iPhone 11 Pro Max, light mode")

      self.content
        .environment(\.colorScheme, .dark)
        .preferredColorScheme(.dark)
        .navigationBarHidden(true)
        .previewDevice("iPhone 11 Pro Max")
        .previewDisplayName("iPhone 11 Pro Max, dark mode")

      self.content
        .environment(\.colorScheme, .light)
        .preferredColorScheme(.light)
        .navigationBarHidden(true)
        .previewDevice("iPhone 8")
        .previewDisplayName("iPhone 8, light mode")

      self.content
        .environment(\.colorScheme, .dark)
        .preferredColorScheme(.dark)
        .navigationBarHidden(true)
        .previewDevice("iPhone 8")
        .previewDisplayName("iPhone 8, dark mode")

      self.content
        .environment(\.colorScheme, .light)
        .preferredColorScheme(.light)
        .navigationBarHidden(true)
        .previewDevice("iPad Air (5th generation)")
        .previewDisplayName("iPad Air, light mode")

      self.content
        .environment(\.colorScheme, .dark)
        .preferredColorScheme(.dark)
        .navigationBarHidden(true)
        .previewDevice("iPad Air (5th generation)")
        .previewDisplayName("iPad Air, dark mode")

      self.content
        .environment(\.colorScheme, .light)
        .preferredColorScheme(.light)
        .navigationBarHidden(true)
        .previewDevice("iPhone 11 Pro Max")
        .previewDisplayName("Accessibility")
        .emergeAccessibility(true)
    }
  }
}

struct PreviewHelpers {
  private static var referenceTimestamp: Int64 {
    let calendar = Calendar.current
    let oneYearAgo = calendar.date(byAdding: .hour, value: -1, to: Date())!
    return Int64(oneYearAgo.timeIntervalSince1970)
  }

  static func withNavigationView(@ViewBuilder content: () -> some View)
    -> some View
  {
    NavigationStack {
      ZStack {
        HNColors.background
          .ignoresSafeArea()

        content()
      }
      .toolbarColorScheme(.dark, for: .navigationBar)
      .toolbarBackground(HNColors.orange, for: .navigationBar)
      .toolbarBackground(.visible, for: .navigationBar)
    }
  }

  static func makeFakeStories() -> [Story] {
    return (0..<20).map { index in
      makeFakeStory(index: index)
    }
  }
  
  static func makeFakeLoadingStories() -> [StoryState] {
    return (0..<10).map { index in StoryState.loading(id: index) }
  }

  static func makeFakeStory(
    index: Int64 = 0, descendants: Int = 0, kids: [Int64]? = nil
  ) -> Story {
    return Story(
      id: index,
      by: "dang",
      time: referenceTimestamp - (index * 3600),  // Each story 1 hour apart
      type: .story,
      title: "Test story \(index)",
      text: "Test story body \(index)",
      url: "https://emergetools.com",
      score: 100,
      descendants: descendants,
      kids: kids
    )
  }

  static func makeFakeComment(level: Int = 0) -> CommentState {
    CommentState(
      id: 1,
      upvoted: false,
      upvoteUrl: "",
      text: """
        Totally useless commentary:
        It makes me deeply happy to hear success stories like this for a project that's moving in the correctly opposite direction to that of the rest of the world.

        Engildification. Of which there should be more!

        My soul was also satisfied by the Sleeping At Night post which, along with the recent "Lie Still in Bed" article, makes for very simple options to attempt to fix sleep (discipline) issues
        """,
      user: "dang",
      age: "10 minutes ago",
      level: level,
      hidden: false
    )
  }
}

struct FakeBookmarkDataStore: BookmarksDataStore {
  func fetchBookmarks() -> [Bookmark] {
    return []
  }
  
  func addBookmark(_ bookmark: Bookmark) {
  }

  func removeBookmark(with id: Int64) {
  }

  func containsBookmark(with id: Int64) -> Bool {
    return false
  }
}
