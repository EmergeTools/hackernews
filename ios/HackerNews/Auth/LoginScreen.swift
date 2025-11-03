//
//  LoginScreen.swift
//  HackerNews
//
//  Created by Rikin Marfatia on 12/30/24.
//

import Foundation
import SwiftUI
import Common

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

      TextField(String(localized: "auth.field.username"), text: $loginState.username)
        .textInputAutocapitalization(.none)
        .padding()
        .background(Color.background)
        .clipShape(RoundedRectangle(cornerRadius: 12))
        .overlay(
          RoundedRectangle(cornerRadius: 12)
            .stroke(Color.background.opacity(0.5), lineWidth: 1)
        )

      SecureField(String(localized: "auth.field.password"), text: $loginState.password)
        .padding()
        .background(Color.background)
        .clipShape(RoundedRectangle(cornerRadius: 12))
        .overlay(
          RoundedRectangle(cornerRadius: 12)
            .stroke(Color.background.opacity(0.5), lineWidth: 1)
        )

      if loginState.showError {
        Text("auth.error.invalidCredentials")
          .foregroundColor(.red)
          .font(.ibmPlexMono(.regular, size: 14))
      }

      
      HStack(spacing: 0) {
        Text("auth.agreement.prefix")
          .font(.ibmPlexSans(.regular, size: 12))
        Text("auth.agreement.guidelines")
          .font(.ibmPlexSans(.regular, size: 12))
          .foregroundColor(.blue)
          .underline()
          .onTapGesture {
            openURL("https://news.ycombinator.com/newsguidelines.html")
          }
        Spacer()
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
          Text("auth.button.submit")
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
  
  func openURL(_ urlString: String) {
    guard let url = URL(string: urlString) else { return }
    UIApplication.shared.open(url, options: [:], completionHandler: nil)
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
