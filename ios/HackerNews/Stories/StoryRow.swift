//
//  Story.swift
//  Hacker News
//
//  Created by Ryan Brooks on 6/21/23.
//

import Foundation
import SwiftUI

struct StoryRow: View {
  @ObservedObject var model: AppViewModel
  let state: StoryState

  var body: some View {
    switch state {
    case .loading:
      StoryRowLoadingState()
    case .nextPage:
      StoryRowLoadingState()
        .onAppear {
          Task {
            await model.fetchNextPage()
          }
        }
    case .loaded(let content):
      VStack(alignment: .leading, spacing: 8) {
        let author = content.author!
        HStack {
          Text("@\(author)")
            .font(.custom("IBMPlexMono-Bold", size: 12))
            .foregroundColor(.hnOrange)
          Spacer()
          if (content.bookmarked) {
            Image(systemName: "book.fill")
              .font(.system(size: 12))
              .foregroundStyle(.hnOrange)
          }
        }
        Text(content.title)
          .font(.custom("IBMPlexMono-Bold", size: 16))
        HStack(spacing: 16) {
          HStack(spacing: 4) {
            Image(systemName: "arrow.up")
              .font(.system(size: 12))
              .foregroundColor(.green)
            Text("\(content.score)")
              .font(.custom("IBMPlexSans-Medium", size: 12))
          }
          HStack(spacing: 4) {
            Image(systemName: "clock")
              .font(.system(size: 12))
              .foregroundColor(.purple)
            Text(content.relativeDate())
              .font(.custom("IBMPlexSans-Medium", size: 12))
          }
          Spacer()
          // Comment Button
          Button(action: {
            print("Pressed comment button for: \(content.id)")
            model.navigationPath.append(
              AppViewModel.AppNavigation.storyComments(story: content.toStory())
            )
          }) {
            HStack(spacing: 4) {
              Image(systemName: "message.fill")
                .font(.system(size: 12))
                .foregroundStyle(.blue)
              Text("\(content.commentCount)")
                .font(.custom("IBMPlexSans-Medium", size: 12))
                .foregroundStyle(.black)
            }
          }
          .buttonStyle(.bordered)
          .buttonBorderShape(ButtonBorderShape.capsule)
        }
      }
      .padding(.horizontal, 8)
      .onTapGesture {
        switch state {
        case .loading, .nextPage:
          print("Hello")
        case .loaded(let content):
          let destination: AppViewModel.AppNavigation = if let url = content.makeUrl() {
            .webLink(url: url, title: content.title)
          } else {
            .storyComments(story: content.toStory())
          }
          print("Navigating to \(destination)")
          model.navigationPath.append(destination)
        }
      }
      .onLongPressGesture {
        if case .loaded(var content) = state {
          content.bookmarked.toggle()
          model.toggleBookmark(content)
        }
      }
    }
  }
}

struct StoryRowLoadingState: View {
  var body: some View {
    VStack(alignment: .leading, spacing: 8) {
      Text("@humdinger")
        .font(.custom("IBMPlexMono-Bold", size: 12))
        .foregroundColor(.hnOrange)
        .redacted(reason: .placeholder)
      Text("Some Short Title")
        .font(.custom("IBMPlexMono-Bold", size: 16))
        .redacted(reason: .placeholder)
      HStack(spacing: 16) {
        HStack(spacing: 4) {
          Image(systemName: "arrow.up")
            .font(.system(size: 12))
            .foregroundColor(.green)
            .redacted(reason: .placeholder)
          Text("99")
            .font(.custom("IBMPlexSans-Medium", size: 12))
            .redacted(reason: .placeholder)
        }
        HStack(spacing: 4) {
          Image(systemName: "clock")
            .font(.system(size: 12))
            .foregroundColor(.purple)
            .redacted(reason: .placeholder)
          Text("2h ago")
            .font(.custom("IBMPlexSans-Medium", size: 12))
            .redacted(reason: .placeholder)
        }
        Spacer()
        // Comment Button
        Button(action: {}) {
          HStack(spacing: 4) {
            Image(systemName: "message.fill")
              .font(.system(size: 12))
              .foregroundStyle(.blue)
            Text("45")
              .font(.custom("IBMPlexSans-Medium", size: 12))
              .foregroundStyle(.black)
          }
        }
        .disabled(true)
        .buttonStyle(.bordered)
        .buttonBorderShape(ButtonBorderShape.capsule)
        .redacted(reason: .placeholder)
      }
    }
    .padding(.horizontal, 8)
  }
}

struct StoryRow_Preview: PreviewProvider {
  static var previews: some View {
    let fakeStory = PreviewHelpers.makeFakeStory(index: 0, descendants: 3, kids: [1, 2, 3])
    PreviewVariants {
      StoryRow(model: AppViewModel(
        bookmarkStore: FakeBookmarkDataStore()
      ), state: .loaded(content: fakeStory.toStoryContent()))
    }
  }
}

struct StoryRowLoadingState_Preview: PreviewProvider {
  static var previews: some View {
    PreviewVariants {
      StoryRowLoadingState()
    }
  }
}

