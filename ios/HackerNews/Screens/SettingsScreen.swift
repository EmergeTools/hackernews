//
//  SettingsScreen.swift
//  HackerNews
//
//  Created by Rikin Marfatia on 12/3/24.
//

import Foundation
import SwiftUI

struct SettingsScreen: View {
  @ObservedObject var model: AppViewModel
  @State var shouldPresentSheet = false
  var body: some View {
    List {
      HStack {
        Text("Login")
      }
      .onTapGesture {
        shouldPresentSheet.toggle()
      }
    }
    .sheet(isPresented: $shouldPresentSheet) {
      LoginScreen(model: model)
    }
  }
}


#Preview {
  SettingsScreen(model: AppViewModel())
}
