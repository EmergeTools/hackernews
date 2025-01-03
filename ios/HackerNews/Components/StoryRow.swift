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
      ProgressView()
    case .nextPage:
      HStack {
        Spacer()
        ProgressView()
        Spacer()
      }
      .onAppear {
        Task {
          await model.fetchNextPage()
        }
      }
    case .loaded(let story):
      VStack(alignment: .leading, spacing: 8) {
        let author = story.by!
        Text("@\(author)")
          .foregroundColor(.hnOrange)
          .fontWeight(/*@START_MENU_TOKEN@*/.bold/*@END_MENU_TOKEN@*/)
        Text(story.title)
          .font(.headline)
        HStack(spacing: 16) {
          HStack(spacing: 4) {
            Image(systemName: "arrow.up")
              .foregroundColor(.green)
            Text("\(story.score)")
          }
          HStack(spacing: 4) {
            Image(systemName: "clock")
              .foregroundColor(.purple)
            Text(story.displayableDate)
          }
          Spacer()
          // Comment Button
          Button(action: {
            print("Pressed comment button for: \(story.id)")
            model.navigationPath.append(
              AppViewModel.AppNavigation.storyComments(story: story)
            )
          }) {
            HStack(spacing: 4) {
              Image(systemName: "message.fill")
              Text("\(story.commentCount)")
            }
          }
          .buttonStyle(.bordered)
          .buttonBorderShape(ButtonBorderShape.capsule)
        }
      }
    }
  }
}

struct StoryRow_Preview: PreviewProvider {
  static var previews: some View {
    let fakeStory = PreviewHelpers.makeFakeStory(index: 0, descendants: 3, kids: [1, 2, 3])
    PreviewVariants {
      StoryRow(model: AppViewModel(), state: .loaded(story: fakeStory))
    }
  }
}
