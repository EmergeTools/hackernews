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

struct PostListScreen_Previews: PreviewProvider {
  static var previews: some View {
    let appState = AppViewModel()
    appState.storiesState = .loaded(stories: makeFakeStories())
    
    let loading = AppViewModel()
    loading.authState = .loggedIn
    loading.storiesState = .loading
    
    let loggedIn = AppViewModel()
    loggedIn.authState = .loggedIn
    
    return Group {
      withNavigationView {
        PostListScreen(appState: appState)
          .previewDisplayName("With posts")
      }
      
      withNavigationView {
        PostListScreen(appState: appState)
      }
      .colorScheme(.dark)
      .previewDisplayName("With posts, dark mode")
      
      withNavigationView {
        PostListScreen(appState: loading)
      }
      .previewDisplayName("Loading")
      
      withNavigationView {
        PostListScreen(appState: loading)
      }
      .colorScheme(.dark)
      .previewDisplayName("Loading, dark mode")
      
      withNavigationView {
        PostListScreen(appState: loggedIn)
      }
      .previewDisplayName("No posts")
      
      withNavigationView {
        PostListScreen(appState: loggedIn)
      }
      .colorScheme(.dark)
      .previewDisplayName("No posts, dark mode")
    }
  }
  
  static func makeFakeStories() -> [Story] {
    return (0..<20).map { index in
      return Story(
        id: index,
        by: "dang",
        time: Int64(1715698922) - Int64(index),
        type: .story,
        title: "Test story \(index)",
        text: "Test story body \(index)",
        url: "https://emergetools.com",
        score: 100,
        descendants: 3,
        kids: nil
      )
    }
  }
}
