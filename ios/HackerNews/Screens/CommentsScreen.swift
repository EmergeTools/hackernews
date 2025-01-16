//
//  PostItemScreen.swift
//  Hacker News
//
//  Created by Trevor Elkins on 8/24/23.
//

import Foundation
import SwiftUI

struct CommentsScreen: View {

  @ObservedObject var model: CommentsViewModel

  var body: some View {
    ScrollView {
      LazyVStack(spacing: 4) {
        Spacer()
          .frame(height: 60)

        // Header
        CommentsHeader(
          state: model.state.headerState,
          likePost: {
            Task {
              await model.likePost(
                upvoted: model.state.headerState.upvoted,
                url: model.state.headerState.upvoteLink
              )
            }
          },
          toggleBody: { model.toggleHeaderBody() }
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
        switch model.state.comments {
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
                  await model.likeComment(commentInfo: info)
                }
              }
            )
          }
        }
      }
      .padding(8)
    }
    .overlay(alignment: .topLeading) {
      Button(action: {
        model.goBack()
      }) {
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
    let viewModel = CommentsViewModel(
      story: PreviewHelpers.makeFakeStory(kids: comments.map { $0.id }),
      path: .constant(NavigationPath())
    )
    viewModel.state.comments = .loaded(comments: comments)
    return PreviewVariants {
      PreviewHelpers.withNavigationView {
        CommentsScreen(model: viewModel)
      }
    }
  }
}
