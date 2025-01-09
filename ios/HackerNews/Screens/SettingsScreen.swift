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

  var body: some View {
    List {
      HStack {
        Circle()
          .fill(model.authState == AuthState.loggedIn ? Color.green : Color.red)
          .frame(width: 6)
        Text(model.authState == AuthState.loggedIn ? "Logout" : "Login")
        Spacer()
        Image(systemName: "message.fill")
          .font(.system(size: 12))
          .foregroundStyle(model.authState == AuthState.loggedIn ? Color.blue : Color.gray)
        Image(systemName: "arrow.up")
          .font(.system(size: 12))
          .foregroundStyle(model.authState == AuthState.loggedIn ? Color.green : Color.gray)
      }
      .onTapGesture {
        model.loginRowTapped()
      }
    }
    .navigationTitle("Settings")
    .sheet(isPresented: $model.showLoginSheet) {
      LoginScreen(model: model)
    }
  }
}


#Preview {
  SettingsScreen(model: AppViewModel())
}
