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
            case .loaded(let story):
              let destination: AppViewModel.AppNavigation = if let url = story.makeUrl() {
                .webLink(url: url, title: story.title)
              } else {
                .storyComments(story: story)
              }
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
  FeedScreen(model: AppViewModel())
}

#Preview("Loading") {
  let appModel = AppViewModel()
  appModel.feedState = FeedState()

  return FeedScreen(model: appModel)
}

#Preview("Has posts") {
  let appModel = AppViewModel()
  let fakeStories = PreviewHelpers
    .makeFakeStories()
    .map { StoryState.loaded(story: $0) }
  appModel.feedState = FeedState(stories: fakeStories)

  return FeedScreen(model: appModel)
}
