//
//  Hacker_NewsApp.swift
//  Hacker News
//
//  Created by Trevor Elkins on 6/20/23.
//

import SwiftUI

@main
struct Hacker_NewsApp: App {
  
  @StateObject var appState = AppViewModel()
  
  var body: some Scene {
    WindowGroup {
      NavigationView {
        ZStack {
          HNColors.background
            .edgesIgnoringSafeArea(.all)
          ContentView(appState: appState)
        }
        .toolbarColorScheme(.dark, for: .navigationBar)
        .toolbarBackground(HNColors.orange, for: .navigationBar)
        .toolbarBackground(.visible, for: .navigationBar)
      }
    }
  }
}
