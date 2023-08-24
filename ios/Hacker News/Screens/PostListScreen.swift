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
      if appState.isLoadingPosts {
        ProgressView()
          .progressViewStyle(CircularProgressViewStyle())
          .scaleEffect(2)
      } else {
        List(appState.stories, id: \.id) { story in
          if let url = story.makeUrl() {
            NavigationLink(
              destination: WebView(url: url)
                .ignoresSafeArea()
                .navigationTitle(story.title)
                .navigationBarTitleDisplayMode(.inline)
            ) {
              StoryRow(
                story: story,
                index: appState.stories.firstIndex(where: { $0.id == story.id })!)
            }
            .listRowBackground(Color.clear)
          } else {
            StoryRow(
              story: story,
              index: appState.stories.firstIndex(where: { $0.id == story.id })!)
            .listRowBackground(Color.clear)
          }
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
    appState.stories = makeFakeStories()
    
    let loading = AppViewModel()
    loading.authState = .loggedIn
    loading.isLoadingPosts = true
    
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
        ContentView(appState: loggedIn)
      }
      .previewDisplayName("No posts")
      
      withNavigationView {
        ContentView(appState: loggedIn)
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
        time: Int64(Date().timeIntervalSince1970) - Int64(index),
        type: .story,
        title: "Test story \(index)",
        text: "Test story body \(index)",
        url: nil,
        score: 100,
        descendants: 0,
        kids: nil
      )
    }
  }
}
