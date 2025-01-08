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

      Image(systemName: "bolt.horizontal.circle.fill")
        .foregroundStyle(HNColors.orange)
        .font(.system(size: 64))

      Spacer()
        .frame(maxHeight: 16)

      TextField("Username", text: $model.loginState.username)
        .textFieldStyle(.roundedBorder)
        .autocapitalization(.none)

      SecureField("Password", text: $model.loginState.password)
        .textFieldStyle(.roundedBorder)

      Spacer()
        .frame(maxHeight: 16)

      Button(
        action: {
          Task {
            await model.login()
          }
        },
        label: {
          Text("Login")
            .frame(maxWidth: .infinity)
        }
      )
      .buttonStyle(.borderedProminent)
      .disabled(model.loginState.username.isEmpty || model.loginState.password.isEmpty)
    }
    .frame(maxWidth: .infinity, maxHeight: .infinity)
    .padding(32)
    .background(HNColors.background)
  }
}

#Preview {
  LoginScreen(model: .init())
}
