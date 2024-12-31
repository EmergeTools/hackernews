//
//  PostListScreen.swift
//  Hacker News
//
//  Created by Trevor Elkins on 6/20/23.
//

import Foundation
import SwiftUI

struct PostListScreen: View {
  
  @ObservedObject var appState: AppViewModel
  
  var body: some View {
    VStack {
      HStack(spacing: 16) {
        ForEach(appState.postListState.feeds, id: \.self) { feedType in
          Button(action: {
            Task {
              await appState.fetchInitialPosts(feedType: feedType)
            }
          }) {
            Text(feedType.title)
              .foregroundColor(appState.postListState.selectedFeed == feedType ? .hnOrange : .gray)
              .fontWeight(appState.postListState.selectedFeed == feedType ? .bold : .regular)
              .font(.title2)
          }
        }
      }
      .padding(8)

      List(appState.postListState.stories, id: \.id) { storyState in
        StoryRow(
          model: appState,
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
        await appState.fetchInitialPosts(feedType: .top)
      }
    }
    .navigationTitle("")
    .navigationBarHidden(true)
  }
}

#Preview {
  PostListScreen(appState: AppViewModel())
}

#Preview("Loading") {
  let appModel = AppViewModel()
  appModel.postListState = PostListState()

  return PostListScreen(appState: appModel)
}

#Preview("Has posts") {
  let appModel = AppViewModel()
  let fakeStories = PreviewHelpers
    .makeFakeStories()
    .map { StoryState.loaded(story: $0) }
  appModel.postListState = PostListState(stories: fakeStories)

  return PostListScreen(appState: appModel)
}
