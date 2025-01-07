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
  let state: CommentInfo
  let likeComment: (CommentInfo) -> Void

  var body: some View {
    VStack(alignment: .leading) {
      // first row
      HStack {
        // author
        Text("@\(state.user)")
          .font(.caption)
          .fontWeight(.bold)
        // time
        HStack(alignment: .center, spacing: 4.0) {
          Image(systemName: "clock")
          Text(state.age)
        }
        .font(.caption)
        // collapse/expand
        Image(systemName: "chevron.up.chevron.down")
          .font(.caption)
        // space between
        Spacer()
        // upvote
        Button(action: {
          likeComment(state)
        }) {
          Image(systemName: "arrow.up")
            .font(.caption2)
        }
        .padding(
          EdgeInsets(
            top: 4.0,
            leading: 8.0,
            bottom: 4.0,
            trailing: 8.0
          )
        )
        .background(HNColors.background)
        .foregroundStyle(.black)
        .clipShape(Capsule())
      }
      
      // Comment Body
      Text(state.text.strippingHTML())
        .font(.caption)
    }
    .padding(8.0)
    .background(HNColors.commentBackground)
    .clipShape(RoundedRectangle(cornerRadius: 16.0))
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
        likeComment: {_ in}
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
          likeComment: {_ in}
        )
          .previewLayout(.sizeThatFits)
          .previewDisplayName("Indentation \(index)")
      }
    }
  }
}
