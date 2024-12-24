//
//  CommentsHeader.swift
//  HackerNews
//
//  Created by Rikin Marfatia on 12/23/24.
//

import SwiftUI


struct CommentsHeader: View {
  let state: CommentsHeaderState
  let toggleBody: () -> Void

  var body: some View {
    VStack(alignment: .leading) {
      // title
      Text(state.story.title)
        .font(.title2)
        .fontWeight(.bold)
        .frame(maxWidth: .infinity, alignment: .leading)

      // body
      if (state.story.text != nil) {
        VStack(alignment: .leading, spacing: 8.0) {
          Image(systemName: "chevron.up.chevron.down")
            .font(.caption2)
          Text(state.story.text!)
            .frame(maxWidth: .infinity, alignment: .leading)
            .font(.caption)
            .lineLimit(state.expanded ? nil : 4)
        }
        .padding(8.0)
        .background(Color.commentBackground)
        .clipShape(RoundedRectangle(cornerRadius: 8.0, style: .continuous))
        .onTapGesture {
          toggleBody()
        }
      }

      // actions
      HStack {
        // post author
        let author = state.story.by != nil ? state.story.by! : ""
        Text("@\(author)")
          .font(.caption)
          .fontWeight(.bold)
          .foregroundColor(Color.hnOrange)
        // post time
        HStack(alignment: .center, spacing: 4.0) {
          Image(systemName: "clock")
            .font(.caption2)
            .foregroundStyle(.purple)
          Text(state.story.displayableDate)
            .font(.caption)
        }
        Spacer()
        // upvote button
        Button(action: {}) {
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
    }
  }
}

#Preview {
  CommentsHeader(
    state: CommentsHeaderState(story: PreviewHelpers.makeFakeStory()),
    toggleBody: {}
  )
}
