//
//  CommentRow.swift
//  Hacker News
//
//  Created by Trevor Elkins on 9/5/23.
//

import Foundation
import SwiftUI

struct CommentRow: View {
  let comment: Comment
  let level: Int
  let maxIndentationLevel: Int = 5
  
  var body: some View {
    VStack(alignment: .leading) {
      if let by = comment.by {
        Text(by).font(.caption).foregroundColor(.gray)
      }
      if let text = comment.text?.strippingHTML() {
        Text(text)
      }
    }
    .background(.clear)
    .padding(
      EdgeInsets(
        top: 0,
        leading: min(CGFloat(level * 20), CGFloat(maxIndentationLevel * 20)),
        bottom: 0,
        trailing: 0
      )
    )
  }
}

struct CommentView_Preview: PreviewProvider {
  static var previews: some View {
    Group {
      ForEach(0..<6) { index in
        CommentRow(comment: makeFakeComment(), level: index)
          .previewLayout(.sizeThatFits)
          .previewDisplayName("Indentation \(index)")
      }
    }
  }
}
