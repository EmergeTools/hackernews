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
      case .loaded(let stories):
        List(stories, id: \.id) { story in
          let navigationValue: AppViewModel.AppNavigation = {
            if let url = story.makeUrl() {
              return AppViewModel.AppNavigation.webLink(url: url, title: story.title)
            } else {
              return AppViewModel.AppNavigation.storyComments(story: story)
            }
          }()
          NavigationLink(
            value: navigationValue,
            label: {
              StoryRow(
                appState: appState,
                story: story,
                index: stories.firstIndex(where: { $0.id == story.id })!
              )
            }
          )
          .listRowBackground(Color.clear)
        }
        .listStyle(.plain)
      }
    }
    .navigationBarTitle("Hacker News")
    .toolbar {
      ToolbarItemGroup(placement: .navigationBarTrailing) {
        Button(action: {
          Task {
            await appState.fetchPosts()
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
