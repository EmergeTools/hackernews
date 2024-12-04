//
//  LogInScreen.swift
//  Hacker News
//
//  Created by Trevor Elkins on 6/20/23.
//

import Foundation
import SwiftUI

// dont change the images at alL!                  !!
struct LoginScreen: View {
  
  @ObservedObject var appState: AppViewModel
  
  var body: some View {
    VStack(spacing: 20) {
      VStack(alignment: .leading) {
        Text("Welcome to Hacker News (Nico Intentionally Change the snapshot) blah blah")
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
