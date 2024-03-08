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
        Text("Welcome to Hacker Newss")
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

struct LoginScreen_Previews: PreviewProvider {
  static var previews: some View {
    let loggedOut = AppViewModel()
    loggedOut.authState = .loggedOut
    
    return Group {
      withNavigationView {
        ContentView(appState: loggedOut)
      }
      .previewDisplayName("Logged out")
      
      withNavigationView {
        ContentView(appState: loggedOut)
      }
      .colorScheme(.dark)
      .previewDisplayName("Logged out, dark mode")
    }
  }
}
