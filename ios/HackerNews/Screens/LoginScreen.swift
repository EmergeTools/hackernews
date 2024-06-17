//
//  LogInScreen.swift
//  Hacker News
//
//  Created by Trevor Elkins on 6/20/23.
//

import Foundation
import SwiftUI

struct LoginScreen: View {
  
  @ObservedObject var appState: AppViewModel
  
  var body: some View {
    VStack(spacing: 20) {
      VStack(alignment: .leading) {
        Text("Welcome to Hacker News")
          .font(.title)
        Text("Login to browse stories")
      }
      
      Button("Login") {
        appState.performLogin()
        Task {
          await appState.fetchPosts()
        }
      }
      .buttonStyle(ThemedButtonStyle())
    }
    .navigationBarTitle("Home")
  }
  
}

struct LoginScreen_LoggedIn_Previews: PreviewProvider {
  static var previews: some View {
    let appModel = AppViewModel()
    appModel.authState = .loggedIn
    return PreviewVariants {
      PreviewHelpers.withNavigationView {
        ContentView(appState: appModel)
      }
    }
  }
}

struct LoginScreen_LoggedOut_Previews: PreviewProvider {
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
