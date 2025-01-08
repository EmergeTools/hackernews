//
//  PostListScreen.swift
//  Hacker News
//
//  Created by Trevor Elkins on 6/20/23.
//

import Foundation
import SwiftUI

struct PostListScreen: View {
  
  @ObservedObject var model: AppViewModel
  
  var body: some View {
    VStack {
      HStack(spacing: 16) {
        ForEach(model.postListState.feeds, id: \.self) { feedType in
          Button(action: {
            Task {
              await model.fetchInitialPosts(feedType: feedType)
            }
          }) {
            Text(feedType.title)
              .foregroundColor(model.postListState.selectedFeed == feedType ? .hnOrange : .gray)
              .fontWeight(model.postListState.selectedFeed == feedType ? .bold : .regular)
              .font(.title2)
          }
        }
      }
      .padding(8)

      List(model.postListState.stories, id: \.id) { storyState in
        StoryRow(
          model: model,
          state: storyState
        )
        .background {
          switch storyState {
          case .loading, .nextPage:
            EmptyView()
          case .loaded(let story):
            let destination: AppViewModel.AppNavigation = if let url = story.makeUrl() {
              .webLink(url: url, title: story.title)
            } else {
              .storyComments(story: story)
            }
            NavigationLink(
              value: destination,
              label: {}
            )
            .opacity(0.0)
          }
        }
        .listRowBackground(Color.clear)
      }
      .tag(0)
      .listStyle(.plain)
    }
    .onAppear {
      Task {
        await model.fetchInitialPosts(feedType: .top)
      }
    }
    .navigationTitle("")
    .navigationBarHidden(true)
  }
}

#Preview {
  PostListScreen(model: AppViewModel())
}

#Preview("Loading") {
  let appModel = AppViewModel()
  appModel.postListState = PostListState()

  return PostListScreen(model: appModel)
}

#Preview("Has posts") {
  let appModel = AppViewModel()
  let fakeStories = PreviewHelpers
    .makeFakeStories()
    .map { StoryState.loaded(story: $0) }
  appModel.postListState = PostListState(stories: fakeStories)

  return PostListScreen(model: appModel)
}
