//
//  CommentRow.swift
//  Hacker News
//
//  Created by Trevor Elkins on 9/5/23.
//

import Foundation
import SwiftUI

private let maxIndentationLevel: Int = 5

struct CommentRow: View {
  let state: CommentState
  let likeComment: (CommentState) -> Void
  let toggleComment: () -> Void

  @Environment(Theme.self) private var theme
  @State private var isPressed = false

  var body: some View {
    VStack(alignment: .leading, spacing: 0) {
      // first row
      HStack {
        Group {
          // author
          Text("@\(state.user)")
            .font(theme.commentAuthorFont())
          // time
          HStack(alignment: .center, spacing: 4.0) {
            Image(systemName: "clock")
              .font(.system(size: 12))
            Text(state.age)
              .font(theme.commentMetadataFont())
          }
          .font(.caption)
          // collapse/expand
          Image(systemName: "chevron.up.chevron.down")
            .font(.system(size: 12))
            .rotationEffect(.degrees(state.hidden ? 180 : 0))
          // space between
          Spacer()
          // upvote
          Button(action: {
            likeComment(state)
          }) {
            Image(systemName: "arrow.up")
              .font(.system(size: 12))
              .padding(.horizontal, 8)
              .padding(.vertical, 4)
          }
          .background(state.upvoted ? .green.opacity(0.2) : .white.opacity(0.2))
          .foregroundStyle(state.upvoted ? .green : .onBackground)
          .clipShape(Capsule())
        }
      }
      .padding(8)
      .background(isPressed ? .surface.opacity(0.85) : .surface)
      .zIndex(1)  // Ensure header stays on top

      // Comment Body
      if !state.hidden {
        VStack(alignment: .leading) {
          Text(state.text.strippingHTML())
            .font(theme.commentTextFont())
        }
        .padding(EdgeInsets(top: -3, leading: 8, bottom: 8, trailing: 8))
        .transition(
          .asymmetric(
            insertion: .move(edge: .top).combined(with: .opacity),
            removal: .move(edge: .top).combined(with: .opacity)
          )
        )
      }
    }
    .background(isPressed ? .surface.opacity(0.85) : .surface)
    .clipShape(RoundedRectangle(cornerRadius: 16.0))
    .animation(.spring(duration: 0.3), value: state.hidden)
    .simultaneousGesture(makeCommentGesture())
    .padding(
      EdgeInsets(
        top: 0,
        leading: min(CGFloat(state.level * 20), CGFloat(maxIndentationLevel * 20)),
        bottom: 0,
        trailing: 0)
    )
  }
}

extension CommentRow {
  fileprivate func makeCommentGesture() -> some Gesture {
    DragGesture(minimumDistance: 0)
      .onChanged { value in
        // Only show press effect if we haven't moved far
        if abs(value.translation.height) < 2 && abs(value.translation.width) < 2 {
          isPressed = true
        } else {
          isPressed = false
        }
      }
      .onEnded { value in
        isPressed = false
        // Trigger tap if it was a small movement (effectively a tap)
        if abs(value.translation.height) < 2 && abs(value.translation.width) < 2 {
          toggleComment()
        }
      }
  }
}

struct CommentView_Preview: PreviewProvider {
  static var previews: some View {
    PreviewVariants {
      CommentRow(
        state: PreviewHelpers.makeFakeComment(),
        likeComment: { _ in },
        toggleComment: {}
      )
    }
  }
}

struct CommentViewIndentation_Preview: PreviewProvider {
  static var previews: some View {
    Group {
      ForEach(0..<6) { index in
        CommentRow(
          state: PreviewHelpers.makeFakeComment(level: index),
          likeComment: { _ in },
          toggleComment: {}
        )
        .previewLayout(.sizeThatFits)
        .previewDisplayName("Indentation \(index)")
      }
    }
  }
}
