//
//  BookmarksScreen.swift
//  HackerNews
//
//  Created by Rikin Marfatia on 12/3/24.
//

import Foundation
import SwiftUI

struct BookmarksScreen: View {
  @Binding var model: AppViewModel

  var body: some View {
    Group {
      if model.bookmarks.isEmpty {
        ZStack {
          Text("Long-press a story to bookmark it.")
            .font(.ibmPlexSans(.medium, size: 18))
        }
      } else {
        ScrollView {
          LazyVStack(spacing: 8) {
            ForEach(model.bookmarks, id: \.id) { bookmark in
              StoryRow(
                model: $model,
                state: .loaded(content: bookmark.toStoryContent())
              )

              // Line
              Rectangle()
                .fill(Color.gray.opacity(0.3))
                .frame(height: 1)
            }
          }
        }
      }
    }
    .frame(
      maxWidth: .infinity,
      maxHeight: .infinity
    )
    .safeAreaInset(edge: .top) {
      ZStack(alignment: .leading) {
        Color.clear
          .background(.ultraThinMaterial)
          .containerShape(.rect(cornerRadius: 24, style: .continuous))

        Text("Bookmarks")
          .font(.ibmPlexMono(.bold, size: 24))
          .padding(.horizontal, 16)
      }
      .frame(height: 60)
    }
  }
}

#Preview {
  @Previewable @State var model = AppViewModel(
    bookmarkStore: FakeBookmarkDataStore())
  BookmarksScreen(
    model: $model
  )
}
