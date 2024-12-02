//
//  Previews.swift
//  HackerNews
//
//  Created by Trevor Elkins on 6/17/24.
//

import Foundation
import SnapshotPreferences
import SwiftUI

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

  static func makeFakeComment() -> Comment {
    Comment(
      id: 1,
      by: "dang",
      time: referenceTimestamp,
      type: .comment,
      text: """
        Totally useless commentary:

        Engildification. Of which there should be more!

        """,
      parent: nil,
      kids: nil
    )
  }

  static func makeFakeFlattenedComment(
    comment: Comment = makeFakeComment(), depth: Int = 0
  ) -> FlattenedComment {
    FlattenedComment(comment: comment, depth: depth)
  }
}
