//
//  PostListScreen.swift
//  Hacker News
//
//  Created by Trevor Elkins on 6/20/23.
//

import Foundation
import SwiftUI

struct FeedScreen: View {

  @Binding var model: AppViewModel

  var body: some View {
    List {
      ForEach(model.feedState.stories, id: \.id) { storyState in
        VStack(spacing: 0) {
          StoryRow(
            model: $model,
            state: storyState
          )
        }
        .listRowInsets(EdgeInsets())
        .listRowSeparatorTint(Color.gray.opacity(0.3))
        .contextMenu(
          menuItems: {
            if case .loaded(var content) = storyState {
              Button {
                content.bookmarked.toggle()
                model.toggleBookmark(content)
              } label: {
                Label(
                  content.bookmarked ? "Remove Bookmark" : "Bookmark",
                  systemImage: content.bookmarked ? "book.fill" : "book"
                )
              }

              if let url = content.makeUrl() {
                ShareLink(
                  item: url,
                  preview: SharePreview(
                    content.title,
                    image: Image(systemName: "link")
                  )
                )
              }
            }
          },
          preview: {
            if case .loaded(let content) = storyState,
              let url = content.makeUrl()
            {
              WebView(url: url)
                .frame(width: 300, height: 400)
            }
          }
        )
        .swipeActions(edge: .trailing, allowsFullSwipe: false) {
          if case .loaded(var content) = storyState {
            Button {
              content.bookmarked.toggle()
              model.toggleBookmark(content)
            } label: {
              Label(
                content.bookmarked ? "Remove Bookmark" : "Bookmark",
                systemImage: content.bookmarked ? "book.fill" : "book"
              )
            }
            .tint(.orange)
          }
        }
      }
      .listRowBackground(Color.clear)
    }
    .listStyle(.plain)
    .scrollContentBackground(.hidden)
    .safeAreaInset(edge: .top) {
      ZStack {
        Color.clear
          .background(.ultraThinMaterial)
          .containerShape(.rect(cornerRadius: 24, style: .continuous))

        HStack(spacing: 16) {
          ForEach(model.feedState.feeds, id: \.self) { feedType in
            Button(action: {
              Task {
                await model.fetchInitialPosts(feedType: feedType)
              }
            }) {
              Text(feedType.title)
                .font(.ibmPlexMono(.bold, size: 24))
                .scaleEffect(
                  model.feedState.selectedFeed == feedType ? 1.0 : 0.8
                )
                .foregroundColor(
                  model.feedState.selectedFeed == feedType ? .hnOrange : .gray)
            }
          }
        }
      }
      .frame(height: 60)
    }
  }
}

#Preview {
  @Previewable @State var model = AppViewModel(
    bookmarkStore: FakeBookmarkDataStore())
  FeedScreen(model: $model)
}

#Preview("Loading") {
  @Previewable @State var appModel = AppViewModel(
    bookmarkStore: FakeBookmarkDataStore())
  appModel.feedState = FeedState()

  return FeedScreen(model: $appModel)
}

#Preview("Has posts") {
  @Previewable @State var appModel = AppViewModel(
    bookmarkStore: FakeBookmarkDataStore())
  let fakeStories =
    PreviewHelpers
    .makeFakeStories()
    .map { StoryState.loaded(content: $0.toStoryContent()) }
  appModel.feedState = FeedState(stories: fakeStories)

  return FeedScreen(model: $appModel)
}
