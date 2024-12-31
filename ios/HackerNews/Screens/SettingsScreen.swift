//
//  SettingsScreen.swift
//  HackerNews
//
//  Created by Rikin Marfatia on 12/3/24.
//

import Foundation
import SwiftUI

struct SettingsScreen: View {
  @State var shouldPresentSheet = false
  var body: some View {
    VStack {
      Button("Login") {
        shouldPresentSheet.toggle()
      }
      .sheet(isPresented: $shouldPresentSheet) {
        LoginScreen()
      }
    }
  }
}

#Preview {
  SettingsScreen()
}
