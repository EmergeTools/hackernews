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
      PostListScreen(model: model)
        .tag(1)
        .tabItem { Label("Feed", systemImage: "list.dash") }
      BookmarksScreen()
        .tag(2)
        .tabItem { Label("Bookmarks", systemImage: "book") }
      SettingsScreen(model: model)
        .tag(3)
        .tabItem { Label("Settings", systemImage: "gear") }
    }
  }
}

struct ContentView_LoggedIn_Loading_Previews: PreviewProvider {
  static var previews: some View {
    let appModel = AppViewModel()
    appModel.postListState = PostListState(stories: [])
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

    appModel.postListState = PostListState(stories: fakeStories)

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
    appModel.postListState = PostListState(stories: [])
    return PreviewVariants {
      PreviewHelpers.withNavigationView {
        ContentView(model: appModel)
      }
    }
  }
}
