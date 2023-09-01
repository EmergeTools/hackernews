//
//  Extensions.swift
//  Hacker News
//
//  Created by Trevor Elkins on 8/15/23.
//

import Foundation
import SwiftUI

extension PreviewProvider {
  static func withNavigationView(@ViewBuilder content: () -> some View) -> some View {
    NavigationView {
      ZStack {
        HNColors.background
          .edgesIgnoringSafeArea(.all)
        content()
          .toolbarColorScheme(.dark, for: .navigationBar)
          .toolbarBackground(HNColors.orange, for: .navigationBar)
          .toolbarBackground(.visible, for: .navigationBar)
      }
    }
  }
  
  static func makeFakeStories() -> [Story] {
    return (0..<20).map { index in
      makeFakeStory(index: index)
    }
  }
  
  static func makeFakeStory(index: Int64 = 0) -> Story {
    return Story(
      id: index,
      by: "dang",
      time: Int64(Date().timeIntervalSince1970) - Int64(index),
      type: .story,
      title: "Test story \(index)",
      text: "Test story body \(index)",
      url: "https://emergetools.com",
      score: 100,
      descendants: 0,
      kids: nil
    )
  }
  
  static func makeFakeComment() -> Comment {
    Comment(
      id: 1,
      by: "dang",
      time: Int64(Date.now.timeIntervalSince1970),
      type: .comment,
      text: """
Totally useless commentary:
It makes me deeply happy to hear success stories like this for a project that's moving in the correctly opposite direction to that of the rest of the world.

Engildification. Of which there should be more!

My soul was also satisfied by the Sleeping At Night post which, along with the recent "Lie Still in Bed" article, makes for very simple options to attempt to fix sleep (discipline) issues
""",
      parent: nil,
      kids: nil
    )
  }
  
  static func makeFakeFlattenedComment(comment: Comment = makeFakeComment(), depth: Int = 0) -> FlattenedComment {
    FlattenedComment(comment: comment, depth: depth)
  }
}

extension String {
  func htmlToAttributedString() -> NSAttributedString? {
    guard let data = self.data(using: .utf8) else { return nil }
    return try? NSAttributedString(
      data: data,
      options: [
        .documentType: NSAttributedString.DocumentType.html,
        .characterEncoding: String.Encoding.utf8.rawValue
      ],
      documentAttributes: nil)
  }
}
