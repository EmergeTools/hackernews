//
//  CommentsHeader.swift
//  HackerNews
//
//  Created by Rikin Marfatia on 12/23/24.
//

import SwiftUI

struct CommentsHeader: View {
  let state: CommentsHeaderState
  let likePost: () -> Void
  let flagPost: () -> Void
  let toggleBody: () -> Void
  let onTitleTap: () -> Void

  var body: some View {
    VStack(alignment: .leading) {
      // title - wrap in Button
      Button(action: onTitleTap) {
        Text(state.story.title)
          .font(.ibmPlexMono(.bold, size: 16))
          .frame(maxWidth: .infinity, alignment: .leading)
      }
      .buttonStyle(.plain)

      // actions
      HStack {
        // post author
        let author = state.story.by != nil ? state.story.by! : ""
        Text("@\(author)")
          .font(.ibmPlexMono(.bold, size: 12))
          .foregroundStyle(.hnOrange)
        // post time
        HStack(alignment: .center, spacing: 4.0) {
          Image(systemName: "clock")
            .font(.system(size: 12))
            .foregroundStyle(.purple)
          Text(state.story.displayableDate)
            .font(.ibmPlexSans(.medium, size: 12))
        }
        Spacer()
        // share button
        ShareLink(item: state.story.url ?? "") {
          Image(systemName: "square.and.arrow.up")
            .font(.system(size: 12))
            .padding(.horizontal, 8)
            .padding(.vertical, 4)
        }
        .background(.surface)
        .frame(height: 23)
        .foregroundStyle(.onBackground)
        .clipShape(Capsule())
        // upvote button
        Button(action: { likePost() }) {
          Image(systemName: "arrow.up")
            .font(.system(size: 12))
            .padding(.horizontal, 8)
            .padding(.vertical, 4)
        }
        .frame(height: 23)
        .background(state.upvoted ? .green.opacity(0.2) : .surface)
        .foregroundStyle(state.upvoted ? .green : .onBackground)
        .clipShape(Capsule())
        
        Menu {
          Button("Report Post", action: flagPost)
        } label: {
          Image(systemName: "ellipsis")
            .font(.system(size: 12))
            .padding(.horizontal, 8)
            .padding(.vertical, 4)
            .frame(height: 20)
            .background(.surface)
            .foregroundStyle(.onBackground)
            .clipShape(Capsule())
        }
      }
      // body
      if let text = state.story.text {
        VStack(alignment: .leading, spacing: 8.0) {
          Image(systemName: "chevron.up.chevron.down")
            .font(.caption2)
          Text(text.formattedHTML())
            .font(.ibmPlexMono(.regular, size: 12))
            .frame(maxWidth: .infinity, alignment: .leading)
            .lineLimit(state.expanded ? nil : 4)
        }
        .padding(8.0)
        .background(.surface)
        .clipShape(RoundedRectangle(cornerRadius: 8.0, style: .continuous))
        .onTapGesture {
          toggleBody()
        }
      }
    }
  }
}

#Preview {
  CommentsHeader(
    state: CommentsHeaderState(story: PreviewHelpers.makeFakeStory()),
    likePost: {},
    flagPost: {},
    toggleBody: {},
    onTitleTap: {}
  )
}
