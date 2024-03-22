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
