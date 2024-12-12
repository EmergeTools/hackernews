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
        Text("Login to browse stories")
        Text("Login to browse stories")
        Text("Login to browse stories")
        Text("Login to browse stories")
        Text("Login to browse stories")
      }
      
      Button("Login") {
        appState.performLogin()
        Task {
          await appState.fetchPosts(feedType: .top)
        }
      }
      .buttonStyle(ThemedButtonStyle())
    }
    .navigationBarTitle("Home")
  }
  
}
