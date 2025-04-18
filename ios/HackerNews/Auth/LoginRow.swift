//
//  LoginRow.swift
//  HackerNews
//
//  Created by Rikin Marfatia on 1/17/25.
//

import Foundation
import SwiftUI
import Common

struct LoginRow: View {
  let loggedIn: Bool
  let tapped: () -> Void
  @Environment(Theme.self) private var theme
  
  var body: some View {
    HStack(alignment: .center, spacing: 8) {
      Circle()
        .fill(glowColor())
        .frame(
          width: 8
        )
      Text(loginText())
        .font(theme.themedFont(size: 16, style: .mono, weight: .bold))
      Spacer()
      Image(systemName: "message.fill")
        .font(.system(size: 12))
        .foregroundStyle(messageColor())
      Image(systemName: "arrow.up")
        .font(.system(size: 12))
        .foregroundStyle(likeColor())
    }
    .frame(maxWidth: .infinity, alignment: .leading)
    .padding(16)
    .background(.surface)
    .clipShape(.rect(cornerRadius: 16))
    .onTapGesture {
      tapped()
    }
  }

  func loginText() -> String {
    return loggedIn ? "Logout" : "Login"
  }

  func glowColor() -> Color {
    return loggedIn ? .green : .onBackground.opacity(0.2)
  }

  func messageColor() -> Color {
    return loggedIn ? .blue : .onBackground.opacity(0.2)
  }

  func likeColor() -> Color {
    return loggedIn ? .green : .onBackground.opacity(0.2)
  }
}

#Preview {
  LoginRow(loggedIn: true, tapped: {})
    .environment(Theme())
}
