//
//  Story.swift
//  Hacker News
//
//  Created by Ryan Brooks on 6/21/23.
//

import Foundation
import SwiftUI

struct StoryRow: View {
  @ObservedObject var appState: AppViewModel
  let story: Story
  let index: Int
  
  var body: some View {
    VStack(alignment: .leading) {
      HStack {
        Button(action: {
          // TODO: Add upvote action
          print("Pressed upvote for \(story.title)")
        }) {
          VStack {
            Image(systemName: "arrow.up")
              .foregroundColor(Color.primary)
            Text("\(story.score)")
              .font(.caption)
              .foregroundColor(Color.primary)
          }
        }
        .padding(.trailing, 4)
        
        VStack(alignment: .leading) {
          Text("\(index + 1). \(story.title)")
            .font(.subheadline)
            .foregroundColor(Color.primary)
          
          if let displayableUrl = story.displayableUrl {
            Text("(\(displayableUrl))")
              .font(.caption2)
              .foregroundColor(Color.primary.opacity(0.6))
          }
          HStack {
            let dateAndAuthor: String = {
              if let author = story.by {
                return "\(story.displayableDate) by \(author)"
              } else {
                return story.displayableDate
              }
            }()
            Text(dateAndAuthor)
              .font(.caption)
              .foregroundColor(Color.primary.opacity(0.6))
            
            if story.commentCount > 0 {
              Button(action: {
                appState.navigationPath.append(AppViewModel.AppNavigation.storyComments(story: story))
              }) {
                let commentText: String = {
                  if story.commentCount == 1 {
                    return "\(story.commentCount) comment"
                  } else {
                    return "\(story.commentCount) comments"
                  }
                }()
                Text(commentText)
                  .font(.caption)
                  .fontWeight(.medium)
                  .underline()
                  .foregroundColor(Color.primary)
              }
              .buttonStyle(.borderedProminent)
              .tint(HNColors.commentBackground)
              .frame(maxWidth: .infinity, alignment: .trailing)
            }
          }
          .padding(.horizontal, 2)
          .padding(.top, 2)
        }
      }
    }
    .padding(.top, 4)
  }
}

struct StoryRow_Preview: PreviewProvider {
  static var previews: some View {
    let fakeStory = PreviewHelpers.makeFakeStory(index: 0, descendants: 3, kids: [1, 2, 3])
    PreviewVariants {
      StoryRow(appState: AppViewModel(), story: fakeStory, index: 0)
    }
  }
}
