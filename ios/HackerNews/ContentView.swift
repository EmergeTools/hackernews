//
//  ContentView.swift
//  Hacker News
//
//  Created by Trevor Elkins on 6/20/23..
//

import SwiftUI

struct ContentView: View {
  
  @ObservedObject var appState: AppViewModel
  
  var body: some View {
    switch appState.authState {
    case .loggedIn:
      TabView {
        PostListScreen(appState: appState)
          .tag(1)
          .tabItem { Label("Feed", systemImage: "list.dash") }
        BookmarksScreen()
          .tag(2)
          .tabItem { Label("Bookmarks", systemImage: "book") }
        SettingsScreen()
          .tag(3)
          .tabItem { Label("Settings", systemImage: "gear") }
      }
    case .loggedOut:
      LoginScreen(appState: appState)
    }
  }
  
}

struct ContentView_LoggedOut_Previews: PreviewProvider {
  static var previews: some View {
    let appModel = AppViewModel()
    appModel.authState = .loggedOut
    return PreviewVariants {
      PreviewHelpers.withNavigationView {
        ContentView(appState: appModel)
      }
    }
  }
}

struct ContentView_LoggedIn_Loading_Previews: PreviewProvider {
  static var previews: some View {
    let appModel = AppViewModel()
    appModel.authState = .loggedIn
    appModel.postListState = PostListState(storiesState: .loading)
    return PreviewVariants {
      PreviewHelpers.withNavigationView {
        ContentView(appState: appModel)
      }
    }
  }
}

struct ContentView_LoggedIn_WithPosts_Previews: PreviewProvider {
  static var previews: some View {
    let appModel = AppViewModel()
    appModel.authState = .loggedIn
    appModel.postListState = PostListState(
      storiesState: .loaded(items: PreviewHelpers.makeFakeStories())
    )
    return PreviewVariants {
      PreviewHelpers.withNavigationView {
        ContentView(appState: appModel)
      }
    }
  }
}

struct ContentView_LoggedIn_EmptyPosts_Previews: PreviewProvider {
  static var previews: some View {
    let appModel = AppViewModel()
    appModel.authState = .loggedIn
    appModel.postListState = PostListState(
      storiesState: .loaded(items: [])
    )
    return PreviewVariants {
      PreviewHelpers.withNavigationView {
        ContentView(appState: appModel)
      }
    }
  }
}
