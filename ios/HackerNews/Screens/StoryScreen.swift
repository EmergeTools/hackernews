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
    VStack {
      // Header
      CommentsHeader(
        state: storyModel.state.headerState,
        toggleBody: { storyModel.toggleHeaderBody() }
      )

      // Seperator
      Rectangle()
        .fill()
        .frame(maxWidth: .infinity, maxHeight: 1)

      // Comments
      ZStack {
        switch storyModel.state.comments {
        case .notStarted, .loading:
          ProgressView()
            .progressViewStyle(CircularProgressViewStyle())
            .scaleEffect(2)
        case .loaded(let comments):
          VStack {
            List(comments, id: \.id) { commentInfo in
              CommentRow(comment: commentInfo)
              .listRowBackground(Color.clear)
              .listRowSeparator(.hidden)
              .listRowInsets(.init(top: 0, leading: 0, bottom: 0, trailing: 0))
            }
            .padding(EdgeInsets(top: 4, leading: 0, bottom: 4, trailing: 0))
            .listStyle(.plain)
            .listRowSpacing(4.0)
          }
        }
      }
      .frame(maxHeight: .infinity)
    }
    .padding(8.0)
    .navigationTitle(storyModel.state.headerState.story.title)
    .toolbarBackground(HNColors.orange, for: .navigationBar)
    .toolbarBackground(.visible, for: .navigationBar)
    .toolbar {
      if let url = storyModel.state.headerState.story.makeUrl() {
        ToolbarItemGroup(placement: .navigationBarTrailing) {
          NavigationLink(value: AppViewModel.AppNavigation.webLink(url: url, title: storyModel.state.headerState.story.title)) {
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
      PreviewHelpers.makeFakeComment(),
      PreviewHelpers.makeFakeComment(),
      PreviewHelpers.makeFakeComment(),
      PreviewHelpers.makeFakeComment(),
    ]
    let viewModel = StoryViewModel(story: PreviewHelpers.makeFakeStory(kids: comments.map { $0.id }))
    viewModel.state.comments = .loaded(comments: comments)
    return PreviewVariants {
      PreviewHelpers.withNavigationView {
        StoryScreen(storyModel: viewModel)
      }
    }
  }
}
