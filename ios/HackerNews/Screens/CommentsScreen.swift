//
//  PostItemScreen.swift
//  Hacker News
//
//  Created by Trevor Elkins on 8/24/23.
//

import Foundation
import SwiftUI

struct CommentsScreen: View {

  @ObservedObject var storyModel: CommentsViewModel

  var body: some View {
    ScrollView {
      LazyVStack(spacing: 4) {
        Spacer()
          .frame(height: 60)

        // Header
        CommentsHeader(
          state: storyModel.state.headerState,
          toggleBody: { storyModel.toggleHeaderBody() }
        )

        // Line Seperator
        Spacer()
          .frame(height: 8)
        Rectangle()
          .fill(.onBackground)
          .frame(height: 1)
          .frame(maxWidth: .infinity)
        Spacer()
          .frame(height: 8)

        // Comments
        switch storyModel.state.comments {
        case .notStarted, .loading:
          ProgressView()
            .progressViewStyle(CircularProgressViewStyle())
            .scaleEffect(2)
        case .loaded(let comments):
          ForEach(comments, id: \.id) { commentInfo in
            CommentRow(
              state: commentInfo,
              likeComment: { info in
                Task {
                  await storyModel.likeComment(comment: info)
                }
              }
            )
          }
        }
      }
      .padding(8)
    }
    .overlay(alignment: .topLeading) {
      Button(action: {}) {
        Image(systemName: "arrow.left")
          .foregroundStyle(.onBackground)
          .padding(8)
      }
      .background(.ultraThinMaterial)
      .clipShape(Circle())
      .padding(8)
    }
  }
}

struct StoryScreen_Preview: PreviewProvider {
  static var previews: some View {
    let comments = [
      PreviewHelpers.makeFakeComment(),
      PreviewHelpers.makeFakeComment(),
      PreviewHelpers.makeFakeComment(),
      PreviewHelpers.makeFakeComment(),
    ]
    let viewModel = CommentsViewModel(story: PreviewHelpers.makeFakeStory(kids: comments.map { $0.id }))
    viewModel.state.comments = .loaded(comments: comments)
    return PreviewVariants {
      PreviewHelpers.withNavigationView {
        CommentsScreen(storyModel: viewModel)
      }
    }
  }
}
