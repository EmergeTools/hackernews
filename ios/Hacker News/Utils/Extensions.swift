//
//  Extensions.swift
//  Hacker News
//
//  Created by Trevor Elkins on 8/15/23.
//

import Foundation
import SwiftUI
import SwiftSoup

extension PreviewProvider {
  static func withNavigationView(@ViewBuilder content: () -> some View) -> some View {
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
  
  static func makeFakeStory(index: Int64 = 0, descendants: Int = 0, kids: [Int64]? = nil) -> Story {
    return Story(
      id: index,
      by: "dang",
      time: Int64(Date().timeIntervalSince1970) - Int64(index),
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
  func strippingHTML() -> String {
    guard let doc: Document = try? SwiftSoup.parse(self) else { return "" } // parse html
    return (try? doc.text()) ?? ""
  }
}
