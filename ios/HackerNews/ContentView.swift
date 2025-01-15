//
//  ContentView.swift
//  Hacker News
//
//  Created by Trevor Elkins on 6/20/23..
//

import SwiftUI

struct ContentView: View {

  @ObservedObject var model: AppViewModel

  var body: some View {
    TabView {
      FeedScreen(model: model)
        .tabItem {
          Image(systemName: "newspaper.fill")
        }
      BookmarksScreen()
        .tabItem {
          Image(systemName: "book")
        }
      SettingsScreen(model: model)
        .tabItem {
          Image(systemName: "gear")
        }
    }
    .accentColor(.hnOrange)
  }
}

struct ContentView_LoggedIn_Loading_Previews: PreviewProvider {
  static var previews: some View {
    let appModel = AppViewModel()
    appModel.feedState = FeedState(stories: [])
    return PreviewVariants {
      PreviewHelpers.withNavigationView {
        ContentView(model: appModel)
      }
    }
  }
}

struct ContentView_LoggedIn_WithPosts_Previews: PreviewProvider {
  static var previews: some View {
    let appModel = AppViewModel()
    let fakeStories = PreviewHelpers
      .makeFakeStories()
      .map { StoryState.loaded(story: $0) }
    
    appModel.authState = .loggedIn
    appModel.feedState = FeedState(stories: fakeStories)
    
    return PreviewVariants {
      PreviewHelpers.withNavigationView {
        ContentView(model: appModel)
      }
    }
  }
}

struct ContentView_LoggedIn_EmptyPosts_Previews: PreviewProvider {
  static var previews: some View {
    let appModel = AppViewModel()
    appModel.feedState = FeedState(stories: [])
    return PreviewVariants {
      PreviewHelpers.withNavigationView {
        ContentView(model: appModel)
      }
    }
  }
}
