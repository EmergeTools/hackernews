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
          let navigationValue: Hacker_NewsApp.AppNavigation = {
            if story.commentCount == 0 {
              return Hacker_NewsApp.AppNavigation.webLink(url: story.makeUrl()!, title: story.title)
            } else {
              return Hacker_NewsApp.AppNavigation.storyComments(story: story)
            }
          }()
          NavigationLink(
            value: navigationValue,
            label: {
              StoryRow(
                story: story,
                index: appState.stories.firstIndex(where: { $0.id == story.id })!
              )
            }
          )
          .listRowBackground(Color.clear)
        }
        .listStyle(.plain)
        .background(.clear)
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
}
