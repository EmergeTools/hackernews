//
//  PostItemScreen.swift
//  Hacker News
//
//  Created by Trevor Elkins on 8/24/23.
//

import Foundation
import SwiftUI

struct StoryScreen: View {
  
  @ObservedObject var storyModel: StoryViewModel
  
  var body: some View {
    Group {
      if storyModel.isLoadingComments {
        ProgressView()
          .progressViewStyle(CircularProgressViewStyle())
          .scaleEffect(2)
      } else {
        ScrollView {
          LazyVStack {
            ForEach(storyModel.comments) { (flattenedComment) in
              CommentView(comment: flattenedComment.comment, level: flattenedComment.depth)
            }
          }
          .background(.clear)
          .padding()
        }
      }
    }
    .background(.clear)
    .navigationTitle(storyModel.story.title)
    .navigationBarTitleDisplayMode(.inline)
    .toolbarColorScheme(.dark, for: .navigationBar)
    .toolbarBackground(HNColors.orange, for: .navigationBar)
    .toolbarBackground(.visible, for: .navigationBar)
  }
}

struct CommentView: View {
  let comment: Comment
  let level: Int
  let maxIndentationLevel: Int = 5
  
  var body: some View {
    VStack(alignment: .leading) {
      if let by = comment.by {
        Text(by).font(.caption).foregroundColor(.gray)
      }
      if let text = comment.text {
        Text(text.htmlToAttributedString()?.string ?? "")
      }
    }
    .background(.clear)
    .padding(
      EdgeInsets(
        top: 4,
        leading: min(CGFloat(level * 20), CGFloat(maxIndentationLevel * 20)),
        bottom: 4,
        trailing: 0
      )
    )
  }
}

struct StoryScreen_Preview: PreviewProvider {
  static var previews: some View {
    Group {
      StoryScreen(storyModel: StoryViewModel(story: Story(id: 1, by: "dang", time: Int64(Date.now.timeIntervalSince1970), type: .story, title: "Test story", text: "Test text", url: "emergetools.com", score: 100, descendants: 5, kids: nil)))
    }
  }
}

struct CommentView_Preview: PreviewProvider {
  static var previews: some View {
    let comment = Comment(
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
    Group {
      CommentView(comment: comment, level: 0)
        .previewLayout(.sizeThatFits)
      CommentView(comment: comment, level: 1)
        .previewLayout(.sizeThatFits)
      CommentView(comment: comment, level: 2)
        .previewLayout(.sizeThatFits)
      CommentView(comment: comment, level: 3)
        .previewLayout(.sizeThatFits)
      CommentView(comment: comment, level: 4)
        .previewLayout(.sizeThatFits)
      CommentView(comment: comment, level: 5)
        .previewLayout(.sizeThatFits)
    }
  }
}
