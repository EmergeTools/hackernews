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

struct PostListScreen_Loading_Previews: PreviewProvider {
  static var previews: some View {
    let appState = AppViewModel()
    appState.authState = .loggedIn
    appState.storiesState = .loading
    return PreviewVariants {
      PostListScreen(appState: appState)
    }
  }
}

struct PostListScreen_WithPosts_Previews: PreviewProvider {
  static var previews: some View {
    let appState = AppViewModel()
    appState.authState = .loggedIn
    appState.storiesState = .loaded(stories: PreviewHelpers.makeFakeStories())
    return PreviewVariants {
      PostListScreen(appState: appState)
    }
  }
}

struct PostListScreen_EmptyPosts_Previews: PreviewProvider {
  static var previews: some View {
    let appState = AppViewModel()
    appState.authState = .loggedIn
    appState.storiesState = .loaded(stories: [])
    return PreviewVariants {
      PostListScreen(appState: appState)
    }
  }
}
