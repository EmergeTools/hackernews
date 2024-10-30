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
      PostListScreen(appState: appState)
    case .loggedOut:
      LoginScreen(appState: appState)
    }
  }
  
}

struct ContentView_LoggedOut_Default_Previews: PreviewProvider {
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
    appModel.storiesState = .loading
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
    appModel.storiesState = .loaded(stories: PreviewHelpers.makeFakeStories())
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
    appModel.storiesState = .loaded(stories: [])
    return PreviewVariants {
      PreviewHelpers.withNavigationView {
        ContentView(appState: appModel)
      }
    }
  }
}
