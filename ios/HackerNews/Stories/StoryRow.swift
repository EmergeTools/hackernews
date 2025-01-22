//
//  Story.swift
//  Hacker News
//
//  Created by Ryan Brooks on 6/21/23.
//

import Foundation
import SwiftUI

struct StoryRow: View {
  @Binding var model: AppViewModel
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
      Button {
        switch state {
        case .loading, .nextPage:
          print("Hello")
        case .loaded(let content):
          let destination: AppViewModel.AppNavigation =
            if let url = content.makeUrl() {
              .webLink(url: url, title: content.title)
            } else {
              .storyComments(story: content.toStory())
            }
          print("Navigating to \(destination)")
          model.navigationPath.append(destination)
        }
      } label: {
        VStack(alignment: .leading, spacing: 8) {
          let author = content.author!
          HStack {
            Text("@\(author)")
              .font(.ibmPlexMono(.bold, size: 12))
              .foregroundColor(.hnOrange)
            Spacer()
            if content.bookmarked {
              Image(systemName: "book.fill")
                .font(.system(size: 12))
                .foregroundStyle(.hnOrange)
            }
          }
          Text(content.title)
            .font(.ibmPlexMono(.bold, size: 16))
          HStack(spacing: 16) {
            HStack(spacing: 4) {
              Image(systemName: "arrow.up")
                .font(.system(size: 12))
                .foregroundColor(.green)
              Text("\(content.score)")
                .font(.ibmPlexSans(.medium, size: 12))
            }
            HStack(spacing: 4) {
              Image(systemName: "clock")
                .font(.system(size: 12))
                .foregroundColor(.purple)
              Text(content.relativeDate())
                .font(.ibmPlexSans(.medium, size: 12))
            }
            Spacer()
            // Comment Button
            Button(action: {
              print("Pressed comment button for: \(content.id)")
              model.navigationPath.append(
                AppViewModel.AppNavigation.storyComments(
                  story: content.toStory())
              )
            }) {
              HStack(spacing: 4) {
                Image(systemName: "message.fill")
                  .font(.system(size: 12))
                Text("\(content.commentCount)")
                  .font(.ibmPlexSans(.medium, size: 12))
              }
              .foregroundStyle(.blue)
            }
            .buttonStyle(.bordered)
            .buttonBorderShape(ButtonBorderShape.capsule)
          }
        }
        .padding(.horizontal, 8)
      }
      .buttonStyle(StoryRowButtonStyle())
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
        .font(.ibmPlexMono(.bold, size: 12))
        .foregroundColor(.hnOrange)
        .redacted(reason: .placeholder)
      Text("Some Short Title")
        .font(.ibmPlexMono(.bold, size: 16))
        .redacted(reason: .placeholder)
      HStack(spacing: 16) {
        HStack(spacing: 4) {
          Image(systemName: "arrow.up")
            .font(.system(size: 12))
            .foregroundColor(.green)
            .redacted(reason: .placeholder)
          Text("99")
            .font(.ibmPlexSans(.medium, size: 12))
            .redacted(reason: .placeholder)
        }
        HStack(spacing: 4) {
          Image(systemName: "clock")
            .font(.system(size: 12))
            .foregroundColor(.purple)
            .redacted(reason: .placeholder)
          Text("2h ago")
            .font(.ibmPlexSans(.medium, size: 12))
            .redacted(reason: .placeholder)
        }
        Spacer()
        // Comment Button
        Button(action: {}) {
          HStack(spacing: 4) {
            Image(systemName: "message.fill")
              .font(.system(size: 12))
            Text("45")
              .font(.ibmPlexSans(.medium, size: 12))
          }
          .foregroundStyle(.blue)
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

private struct StoryRowButtonStyle: ButtonStyle {
  func makeBody(configuration: Configuration) -> some View {
    configuration.label
      .background(Color.gray.opacity(configuration.isPressed ? 0.1 : 0))
      .scaleEffect(configuration.isPressed ? 0.98 : 1.0)
      .animation(.easeInOut(duration: 0.1), value: configuration.isPressed)
  }
}

struct StoryRow_Preview: PreviewProvider {
  static var previews: some View {
    let fakeStory = PreviewHelpers.makeFakeStory(
      index: 0, descendants: 3, kids: [1, 2, 3])
    @State var model = AppViewModel(
      bookmarkStore: FakeBookmarkDataStore()
    )
    PreviewVariants {
      StoryRow(
        model: $model, state: .loaded(content: fakeStory.toStoryContent()))
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
