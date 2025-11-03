//
//  CommentComposer.swift
//  HackerNews
//
//  Created by Rikin Marfatia on 1/16/25.
//
import SwiftUI

struct CommentComposer: View {
  @Binding var state: CommentComposerState
  let goToLogin: () -> Void
  let sendComment: () -> Void

  var body: some View {
    VStack {
      HStack(alignment: .center) {
        Image(systemName: "message.fill")
          .font(.system(size: 12))
        Text("comments.composer.title")
          .font(.ibmPlexSans(.medium, size: 12))
      }
      TextField(
        String(localized: "comments.composer.placeholder"),
        text: $state.text
      )
      .textFieldStyle(.roundedBorder)
      .disabled(AuthState.loggedOut == state.loggedIn)
      .submitLabel(.send)
      .onSubmit {
        sendComment()
      }
    }
    .padding(16)
    .background {
      Color
        .clear
        .background(.ultraThinMaterial)
        .containerShape(
          .rect(
            cornerRadii: RectangleCornerRadii(
              topLeading: 24,
              bottomLeading: 0,
              bottomTrailing: 0,
              topTrailing: 24
            ),
            style: .continuous
          )
        )
    }
    .onTapGesture {
      if AuthState.loggedOut == state.loggedIn {
        goToLogin()
      }
    }
  }
}

#Preview {
  CommentComposer(
    state: .constant(
      CommentComposerState(
        parentId: "",
        goToUrl: "",
        hmac: "",
        loggedIn: .loggedIn,
        text: ""
      )
    ),
    goToLogin: {},
    sendComment: {}
  )
}
