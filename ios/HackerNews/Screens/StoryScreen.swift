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
      switch storyModel.state {
      case .notStarted, .loading:
        ProgressView()
          .progressViewStyle(CircularProgressViewStyle())
          .scaleEffect(2)
      case .loaded(let comments):
        VStack {
          CommentsHeader(
            story: storyModel.story
          )
          Rectangle()
            .fill()
            .frame(maxWidth: .infinity, maxHeight: 1)
          List(comments, id: \.id) { flattenedComment in
            CommentRow(
              comment: flattenedComment.comment,
              level: flattenedComment.depth
            )
            .listRowBackground(Color.clear)
            .listRowSeparator(.hidden)
            .listRowInsets(.init(top: 0, leading: 0, bottom: 0, trailing: 0))
          }
          .padding(EdgeInsets(top: 4, leading: 0, bottom: 4, trailing: 0))
          .listStyle(.plain)
          .listRowSpacing(4.0)
        }
        .padding(8.0)
      }
    }
    .background(HNColors.background)
    .navigationTitle(storyModel.story.title)
    .navigationBarTitleDisplayMode(.inline)
    .toolbarColorScheme(.dark, for: .navigationBar)
    .toolbarBackground(HNColors.orange, for: .navigationBar)
    .toolbarBackground(.visible, for: .navigationBar)
    .toolbar {
      if let url = storyModel.story.makeUrl() {
        ToolbarItemGroup(placement: .navigationBarTrailing) {
          NavigationLink(value: AppViewModel.AppNavigation.webLink(url: url, title: storyModel.story.title)) {
            Image(systemName: "globe")
              .foregroundColor(.white)
          }
        }
      }
    }
  }
}

struct StoryScreen_Preview: PreviewProvider {
  static var previews: some View {
    let comments = [
      PreviewHelpers.makeFakeFlattenedComment(),
      PreviewHelpers.makeFakeFlattenedComment(),
      PreviewHelpers.makeFakeFlattenedComment(),
      PreviewHelpers.makeFakeFlattenedComment()
    ]
    let viewModel = StoryViewModel(story: PreviewHelpers.makeFakeStory(kids: comments.map { $0.comment.id }))
    viewModel.state = .loaded(comments: comments)
    return PreviewVariants {
      PreviewHelpers.withNavigationView {
        StoryScreen(storyModel: viewModel)
      }
    }
  }
}
