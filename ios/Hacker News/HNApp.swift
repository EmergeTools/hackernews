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
  
  init() {
    UICollectionView.appearance().backgroundColor = .clear
  }
  
  var body: some Scene {
    WindowGroup {
      NavigationStack {
        ZStack {
          HNColors.background
            .ignoresSafeArea()
          ContentView(appState: appState)
        }
        .toolbarColorScheme(.dark, for: .navigationBar)
        .toolbarBackground(HNColors.orange, for: .navigationBar)
        .toolbarBackground(.visible, for: .navigationBar)
      }
    }
  }
}
