//
//  PostListScreen.swift
//  Hacker News
//
//  Created by Trevor Elkins on 6/20/23.
//

import Foundation
import SwiftUI
import HackerNewsCommon

struct FeedScreen: View {

  @Binding var model: AppViewModel
  @State private var isAnimating = false

  var body: some View {
    VStack(spacing: 8) {
      // Feed type selector header
      ZStack {
        Color.clear
          .background(.ultraThinMaterial)
          .containerShape(.rect(cornerRadius: 24, style: .continuous))

        HStack(spacing: 16) {
          ForEach(model.feedState.feeds, id: \.self) { feedType in
            Button(action: {
              withAnimation {
                model.feedState.selectedFeed = feedType
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

      // Page view for feeds
      TabView(selection: $model.feedState.selectedFeed) {
        ForEach(model.feedState.feeds, id: \.self) { feedType in
          FeedListView(
            model: $model, stories: model.feedState.storiesForFeed(feedType)
          )
          .tag(feedType)
          .onChange(of: model.feedState.selectedFeed) { oldValue, newValue in
            if model.feedState.needsToLoadStories(for: newValue) {
              Task {
                try? await Task.sleep(for: .milliseconds(300))
                await model.fetchInitialPosts(feedType: newValue)
              }
            }
          }
        }
      }
      .tabViewStyle(.page(indexDisplayMode: .never))
    }
  }
}

// New view to contain the list portion
private struct FeedListView: View {
  @Binding var model: AppViewModel
  let stories: [StoryState]

  var body: some View {
    List {
      ForEach(stories, id: \.id) { storyState in
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
      }
      .listRowBackground(Color.clear)
    }
    .listStyle(.plain)
    .scrollContentBackground(.hidden)
    .refreshable {
      try? await Task.sleep(for: .milliseconds(300))
      await model.fetchInitialPosts(feedType: model.feedState.selectedFeed)
    }
  }
}

#Preview {
  @Previewable @State var model = AppViewModel(
    bookmarkStore: FakeBookmarkDataStore(),
    shouldFetchPosts: false)
  FeedScreen(model: $model)
    .environment(Theme())
}

#Preview("Loading") {
  @Previewable @State var appModel = AppViewModel(
    bookmarkStore: FakeBookmarkDataStore(),
    shouldFetchPosts: false)
  appModel.feedState = FeedState()

  return FeedScreen(model: $appModel)
    .environment(Theme())
}

#Preview("Has posts") {
  @Previewable @State var appModel = AppViewModel(
    bookmarkStore: FakeBookmarkDataStore(),
    shouldFetchPosts: false)
  let fakeStories =
    PreviewHelpers
    .makeFakeStories()
    .map { StoryState.loaded(content: $0.toStoryContent()) }
  appModel.feedState = FeedState(stories: fakeStories)

  return FeedScreen(model: $appModel)
    .environment(Theme())
}
