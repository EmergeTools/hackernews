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
  var status: LoginStatus = .uninitialized
}

enum LoginStatus {
  case uninitialized
  case error
  case success
}

struct LoginScreen: View {
  @State var username: String = ""
  @State var password: String = ""
  @ObservedObject var model: AppViewModel

  var body: some View {
    VStack(spacing: 8) {
      TextField("Username", text: $model.loginState.username)
        .padding()
        .overlay(
          RoundedRectangle(cornerRadius: 6)
            .stroke(Color.secondary.opacity(0.5))
        )
        .autocapitalization(.none)

      SecureField("Password", text: $model.loginState.password)
        .padding()
        .overlay(
          RoundedRectangle(cornerRadius: 6)
            .stroke(Color.secondary.opacity(0.5))
        )

      Button(
        action: {
          Task {
            await model.login()
          }
        },
        label: {
          Text("Login")
            .padding(8)
        }
      )
      .buttonStyle(.borderedProminent)
      .buttonBorderShape(.capsule)
      .disabled(model.loginState.username.isEmpty || model.loginState.password.isEmpty)
    }
    .padding(16)
  }
}

#Preview {
  LoginScreen(model: .init())
}
