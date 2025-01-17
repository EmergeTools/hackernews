//
//  PostListScreen.swift
//  Hacker News
//
//  Created by Trevor Elkins on 6/20/23.
//

import Foundation
import SwiftUI

struct FeedScreen: View {
  
  @ObservedObject var model: AppViewModel

  var body: some View {
    ScrollView {
      LazyVStack(spacing: 8) {
        Spacer()
          .frame(height: 60)
        ForEach(model.feedState.stories, id: \.id) { storyState in
          StoryRow(
            model: model,
            state: storyState
          )
          .padding(.horizontal, 8)
          .onTapGesture {
            switch storyState {
            case .loading, .nextPage:
              print("Hello")
            case .loaded(let content):
              let destination: AppViewModel.AppNavigation = if let url = content.makeUrl() {
                .webLink(url: url, title: content.title)
              } else {
                .storyComments(story: content.toStory())
              }
              print("Navigating to \(destination)")
              model.navigationPath.append(destination)
            }
          }
          Rectangle()
            .fill(Color.gray.opacity(0.3))
            .frame(height: 1)
        }
      }
    }
    .overlay {
      ZStack {
        Color.clear
          .background(.ultraThinMaterial)
          .containerShape(.rect(cornerRadius: 24, style: .continuous))

        HStack(spacing: 16) {
          ForEach(model.feedState.feeds, id: \.self) { feedType in
            Button(action: {
              Task {
                await model.fetchInitialPosts(feedType: feedType)
              }
            }) {
              Text(feedType.title)
                .font(.custom("IBMPlexMono-Bold", size: 24))
                .scaleEffect(model.feedState.selectedFeed == feedType ? 1.0 : 0.8)
                .foregroundColor(model.feedState.selectedFeed == feedType ? .hnOrange : .gray)
            }
          }
        }
      }
      .frame(height: 60)
      .frame(maxHeight: .infinity, alignment: .top)
    }
  }
}

#Preview {
  FeedScreen(model: AppViewModel(bookmarkStore: FakeBookmarkDataStore()))
}

#Preview("Loading") {
  let appModel = AppViewModel(bookmarkStore: FakeBookmarkDataStore())
  appModel.feedState = FeedState()

  return FeedScreen(model: appModel)
}

#Preview("Has posts") {
  let appModel = AppViewModel(bookmarkStore: FakeBookmarkDataStore())
  let fakeStories = PreviewHelpers
    .makeFakeStories()
    .map { StoryState.loaded(content: $0.toStoryContent()) }
  appModel.feedState = FeedState(stories: fakeStories)

  return FeedScreen(model: appModel)
}
