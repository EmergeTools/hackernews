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
        List(storyModel.comments, id: \.id) { flattenedComment in
          CommentView(comment: flattenedComment.comment, level: flattenedComment.depth)
            .listRowBackground(Color.clear)
            .listRowSeparator(.hidden)
        }
        .listStyle(.plain)
        .background(.clear)
      }
    }
    .navigationTitle(storyModel.story.title)
    .navigationBarTitleDisplayMode(.inline)
    .toolbarColorScheme(.dark, for: .navigationBar)
    .toolbarBackground(HNColors.orange, for: .navigationBar)
    .toolbarBackground(.visible, for: .navigationBar)
    .toolbar {
      ToolbarItemGroup(placement: .navigationBarTrailing) {
        NavigationLink(value: Hacker_NewsApp.AppNavigation.webLink(url: storyModel.story.makeUrl()!, title: storyModel.story.title)) {
          Image(systemName: "globe")
            .foregroundColor(.white)
        }
      }
    }
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
        top: 0,
        leading: min(CGFloat(level * 20), CGFloat(maxIndentationLevel * 20)),
        bottom: 0,
        trailing: 0
      )
    )
  }
}

struct StoryScreen_Preview: PreviewProvider {
  static var previews: some View {
    let viewModel = StoryViewModel(story: makeFakeStory())
    viewModel.comments = [
      makeFakeFlattenedComment(),
      makeFakeFlattenedComment(),
      makeFakeFlattenedComment()
    ]
    viewModel.isLoadingComments = false
    return Group {
      withNavigationView {
        StoryScreen(storyModel: viewModel)
      }
    }
  }
}

struct CommentView_Preview: PreviewProvider {
  static var previews: some View {
    Group {
      ForEach(0..<6) { index in
        CommentView(comment: makeFakeComment(), level: index)
          .previewLayout(.sizeThatFits)
          .previewDisplayName("Indentation \(index)")
      }
    }
  }
}
