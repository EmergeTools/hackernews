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

  var body: some View {
    VStack(alignment: .leading) {
      // first row
      HStack {
        // author
        Text("@\(state.user)")
          .font(.custom("IBMPlexMono-Bold", size: 12))
        // time
        HStack(alignment: .center, spacing: 4.0) {
          Image(systemName: "clock")
            .font(.system(size: 12))
          Text(state.age)
            .font(.custom("IBMPlexSans-Medium", size: 12))
        }
        .font(.caption)
        // collapse/expand
        Image(systemName: "chevron.up.chevron.down")
          .font(.system(size: 12))
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

      // Comment Body
      if (!state.hidden) {
        Text(state.text.strippingHTML())
          .font(.custom("IBMPlexMono-Regular", size: 12))
      }
    }
    .padding(8.0)
    .background(.surface)
    .clipShape(RoundedRectangle(cornerRadius: 16.0))
    .onTapGesture {
      toggleComment()
    }
    .padding(
      EdgeInsets(
        top: 0,
        leading: min(CGFloat(state.level * 20), CGFloat(maxIndentationLevel * 20)),
        bottom: 0,
        trailing: 0)
    )
  }
}

struct CommentView_Preview: PreviewProvider {
  static var previews: some View {
    PreviewVariants {
      CommentRow(
        state: PreviewHelpers.makeFakeComment(),
        likeComment: {_ in},
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
          likeComment: {_ in},
          toggleComment: {}
        )
          .previewLayout(.sizeThatFits)
          .previewDisplayName("Indentation \(index)")
      }
    }
  }
}
