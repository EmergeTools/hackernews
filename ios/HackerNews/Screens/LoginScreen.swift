//
//  LoginScreen.swift
//  HackerNews
//
//  Created by Rikin Marfatia on 12/30/24.
//

import Foundation
import SwiftUI

struct LoginState {
  var username: String = ""
  var password: String = ""
}

enum LoginStatus {
  case error
  case success
}

struct LoginScreen: View {
  @ObservedObject var model: AppViewModel
  @State var loginState = LoginState()

  var body: some View {
    VStack(spacing: 8) {

      Image(systemName: "bolt.horizontal.circle.fill")
        .foregroundStyle(HNColors.orange)
        .font(.system(size: 64))

      Spacer()
        .frame(maxHeight: 16)

      TextField("Username", text: $loginState.username)
        .textFieldStyle(.roundedBorder)
        .autocapitalization(.none)

      SecureField("Password", text: $loginState.password)
        .textFieldStyle(.roundedBorder)

      Spacer()
        .frame(maxHeight: 16)

      Button(
        action: {
          Task {
            await model.loginTapped(
              username: loginState.username,
              password: loginState.password
            )
          }
        },
        label: {
          Text("Login")
            .frame(maxWidth: .infinity)
        }
      )
      .buttonStyle(.borderedProminent)
      .disabled(loginState.username.isEmpty || loginState.password.isEmpty)
    }
    .frame(maxWidth: .infinity, maxHeight: .infinity)
    .padding(32)
    .background(HNColors.background)
  }
}

#Preview {
  LoginScreen(model: .init())
}
