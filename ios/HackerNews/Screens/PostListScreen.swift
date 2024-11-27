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
    Group {
      switch appState.storiesState {
      case .notStarted, .loading:
        ProgressView()
          .progressViewStyle(CircularProgressViewStyle())
          .scaleEffect(2)
        
      case .loaded(let state):
        VStack {
          HStack(spacing: 16) {
            ForEach(AppViewModel.FeedType.allCases, id: \.self) { feedType in
              Button(action: {
                if (feedType != state.feedType) {
                  Task {
                    await appState.fetchPosts(feedType: feedType)
                  }
                }
              }) {
                Text(feedType.title)
                  .foregroundColor(feedType == state.feedType ? .hnOrange : .gray)
                  .fontWeight(feedType == state.feedType ? .bold : .regular)
                  .font(.system(size: 24))
              }
            }
          }
          TabView(selection: .constant(0)) {
            List(state.stories, id: \.id) { story in
                let navigationValue: AppViewModel.AppNavigation = {
                    if let url = story.makeUrl() {
                        return AppViewModel.AppNavigation.webLink(url: url, title: story.title)
                    } else {
                        return AppViewModel.AppNavigation.storyComments(story: story)
                    }
                }()
                
                StoryRow(
                    model: appState,
                    story: story
                )
                .background(
                    NavigationLink(
                        value: navigationValue,
                        label: {}
                    )
                    .opacity(0.0)
                )
                .listRowBackground(Color.clear)
            }
            .tag(0)
            .listStyle(.plain)
          }
          .tabViewStyle(.page)
        }
      }
    }
    .navigationBarTitle("Hacker News")
    .toolbar {
      ToolbarItemGroup(placement: .navigationBarTrailing) {
        Button(action: {
          Task {
            await appState.fetchPosts(feedType: .top)
          }
        }) {
          Image(systemName: "arrow.counterclockwise")
            .foregroundColor(.white)
        }
        Button(action: {
          appState.performLogout()
        }) {
          Image(systemName: "rectangle.portrait.and.arrow.right")
            .foregroundColor(.white)
        }
      }
    }
  }
  
}

#Preview {
  PostListScreen(appState: AppViewModel())
}

#Preview("Loading") {
  let appModel = AppViewModel()
  appModel.authState = .loggedIn
  appModel.storiesState = .loading
  return PostListScreen(appState: appModel)
}

#Preview("Has posts") {
  let appModel = AppViewModel()
  appModel.authState = .loggedIn
  appModel.storiesState = .loaded(state: AppViewModel.StoriesState(stories: PreviewHelpers.makeFakeStories()))
  return PostListScreen(appState: appModel)
}
