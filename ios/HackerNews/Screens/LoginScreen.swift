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
  @ObservedObject var model: AppViewModel

  var body: some View {
    List {
      TextField(text: $model.loginState.username) {
        Text("Username")
      }
      .autocapitalization(.none)
      SecureField(text: $model.loginState.password) {
        Text("Password")
      }
      .autocapitalization(.none)
      Button("Login") {
        Task {
          await model.login()
        }
      }
    }
  }
}

#Preview {
  LoginScreen(model: .init())
}
