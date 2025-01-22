//
//  LoginRow.swift
//  HackerNews
//
//  Created by Rikin Marfatia on 1/17/25.
//

import Foundation
import SwiftUI

struct LoginRow: View {
  let loggedIn: Bool
  let tapped: () -> Void

  var body: some View {
    HStack(alignment: .center, spacing: 8) {
      Circle()
        .fill(glowColor())
        .frame(
          width: 8
        )
      Text(loginText())
        .font(.ibmPlexMono(.bold, size: 16))
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
}
