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
              await appState.fetchPosts(feedType: feedType)
            }
          }) {
            Text(feedType.title)
              .foregroundColor(appState.postListState.selectedFeed == feedType ? .hnOrange : .gray)
              .fontWeight(appState.postListState.selectedFeed == feedType ? .bold : .regular)
              .font(.title)
          }
        }
      }
      .padding(16)
      switch appState.postListState.storiesState {
      case .notStarted, .loading:
        ProgressView()
          .progressViewStyle(CircularProgressViewStyle())
          .scaleEffect(2)
          .frame(maxHeight: .infinity)
      case .loaded(let items):
        TabView(selection: .constant(0)) {
          List(items, id: \.id) { story in
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
//    .navigationBarTitle("Hacker News")
//    .toolbar {
//      ToolbarItemGroup(placement: .navigationBarTrailing) {
//        Button(action: {
//          Task {
//            await appState.fetchPosts(feedType: .top)
//          }
//        }) {
//          Image(systemName: "arrow.counterclockwise")
//            .foregroundColor(.white)
//        }
//        Button(action: {
//          appState.performLogout()
//        }) {
//          Image(systemName: "rectangle.portrait.and.arrow.right")
//            .foregroundColor(.white)
//        }
//      }
//    }
  }
  
}

#Preview {
  PostListScreen(appState: AppViewModel())
}

#Preview("Loading") {
  let appModel = AppViewModel()
  appModel.authState = .loggedIn
  appModel.postListState = PostListState(storiesState: .loading)
  return PostListScreen(appState: appModel)
}

#Preview("Has posts") {
  let appModel = AppViewModel()
  appModel.authState = .loggedIn
  appModel.postListState = PostListState(storiesState: .loaded(items: PreviewHelpers.makeFakeStories()))
  return PostListScreen(appState: appModel)
}
