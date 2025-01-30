//
//  LoginScreen.swift
//  HackerNews
//
//  Created by Rikin Marfatia on 12/30/24.
//

import Foundation
import SwiftUI
import HackerNewsCommon

struct LoginState {
  var username: String = ""
  var password: String = ""
  var showError: Bool = false
}

enum LoginStatus {
  case error
  case success
}

struct LoginScreen: View {
  @Binding var model: AppViewModel
  @State var loginState: LoginState

  init(model: Binding<AppViewModel>, loginState: LoginState = LoginState()) {
    self._model = model
    self._loginState = State(initialValue: loginState)
  }

  var body: some View {
    VStack(spacing: 8) {

      Image(systemName: "person.crop.circle.fill")
        .foregroundStyle(HNColors.orange)
        .font(.system(size: 64))

      Spacer()
        .frame(maxHeight: 16)

      TextField("Username", text: $loginState.username)
        .textInputAutocapitalization(.none)
        .padding()
        .background(Color.background)
        .clipShape(RoundedRectangle(cornerRadius: 12))
        .overlay(
          RoundedRectangle(cornerRadius: 12)
            .stroke(Color.background.opacity(0.5), lineWidth: 1)
        )

      SecureField("Password", text: $loginState.password)
        .padding()
        .background(Color.background)
        .clipShape(RoundedRectangle(cornerRadius: 12))
        .overlay(
          RoundedRectangle(cornerRadius: 12)
            .stroke(Color.background.opacity(0.5), lineWidth: 1)
        )

      if loginState.showError {
        Text("Invalid username or password")
          .foregroundColor(.red)
          .font(.ibmPlexMono(.regular, size: 14))
      }

      Spacer()
        .frame(maxHeight: 16)

      Button(
        action: {
          Task {
            loginState.showError = false  // Reset error state
            let result = await model.loginSubmit(
              username: loginState.username,
              password: loginState.password
            )
            if result == .error {
              withAnimation {
                loginState.showError = true
              }
            }
          }
        },
        label: {
          Text("Submit")
            .font(.ibmPlexMono(.bold, size: 16))
            .frame(maxWidth: .infinity)
            .frame(height: 40)
        }
      )
      .buttonStyle(.borderedProminent)
      .disabled(loginState.username.isEmpty || loginState.password.isEmpty)
    }
    .frame(maxWidth: .infinity, maxHeight: .infinity)
    .padding(32)
    .background(.surface)
  }
}

#Preview("Default") {
  @Previewable @State var model = AppViewModel(
    bookmarkStore: FakeBookmarkDataStore(),
    shouldFetchPosts: false
  )
  LoginScreen(model: $model)
}

#Preview("Error State") {
  struct ErrorStatePreview: View {
    @State var model = AppViewModel(
      bookmarkStore: FakeBookmarkDataStore(),
      shouldFetchPosts: false
    )
    @State var loginState = LoginState(
      username: "test",
      password: "wrong",
      showError: true
    )

    var body: some View {
      LoginScreen(model: $model, loginState: loginState)
    }
  }

  return ErrorStatePreview()
}
