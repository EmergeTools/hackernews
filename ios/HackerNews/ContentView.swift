//
//  ContentView.swift
//  Hacker News
//
//  Created by Trevor Elkins on 6/20/23..
//

import SwiftUI

struct ContentView: View {

  @Binding var model: AppViewModel

  var body: some View {
    TabView {
      FeedScreen(model: $model)
        .tabItem {
          Image(systemName: "newspaper.fill")
        }
      BookmarksScreen(model: $model)
        .onAppear {
          model.fetchBookmarks()
        }
        .tabItem {
          Image(systemName: "book")
        }
      SettingsScreen(model: $model)
        .tabItem {
          Image(systemName: "gear")
        }
    }
    .accentColor(.hnOrange)
  }
}

struct ContentView_LoggedIn_Loading_Previews: PreviewProvider {
  static var previews: some View {
    @Previewable @State var appModel = AppViewModel(bookmarkStore: FakeBookmarkDataStore())
    appModel.feedState = FeedState(stories: [])
    return PreviewVariants {
      PreviewHelpers.withNavigationView {
        ContentView(model: $appModel)
          .environment(Theme())
      }
    }
  }
}

struct ContentView_LoggedIn_WithPosts_Previews: PreviewProvider {
  static var previews: some View {
    @Previewable @State var appModel = AppViewModel(bookmarkStore: FakeBookmarkDataStore())
    let fakeStories = PreviewHelpers
      .makeFakeStories()
      .map { StoryState.loaded(content: $0.toStoryContent()) }
    
    appModel.authState = .loggedIn
    appModel.feedState = FeedState(stories: fakeStories)
    
    return PreviewVariants {
      PreviewHelpers.withNavigationView {
        ContentView(model: $appModel)
          .environment(Theme())
      }
    }
  }
}

struct ContentView_LoggedIn_EmptyPosts_Previews: PreviewProvider {
  static var previews: some View {
    @Previewable @State var appModel = AppViewModel(bookmarkStore: FakeBookmarkDataStore())
    appModel.feedState = FeedState(stories: [])
    return PreviewVariants {
      PreviewHelpers.withNavigationView {
        ContentView(model: $appModel)
          .environment(Theme())
      }
    }
  }
}
