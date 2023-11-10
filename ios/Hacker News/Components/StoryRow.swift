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
    let mockStory = Story.init(
      id: 0,
      by: "Ryan",
      time: Int64(Date().timeIntervalSinceNow),
      type: ItemType.story,
      title: "Mock story for preview",
      text: nil,
      url: "https://emergetools.com",
      score: 100,
      descendants: 3,
      kids: [1, 2, 3]
    )
    let appState = AppViewModel()
    
    Group {
      StoryRow(appState: appState, story: mockStory, index: 0)
        .previewDevice(PreviewDevice(rawValue: "iPhone 14"))
        .previewDisplayName("iPhone 14 test9")
        .previewLayout(.sizeThatFits)
      
      StoryRow(appState: appState, story: mockStory, index: 0)
        .previewDevice(PreviewDevice(rawValue: "iPhone 14 Pro"))
        .previewDisplayName("iPhone 14 Pro")
        .previewLayout(.sizeThatFits)
      
      StoryRow(appState: appState, story: mockStory, index: 0)
        .previewDevice(PreviewDevice(rawValue: "iPhone 14 Pro"))
        .previewDisplayName("iPhone 14 Pro, dark")
        .previewLayout(.sizeThatFits)
        .colorScheme(.dark)
      
      StoryRow(appState: appState, story: mockStory, index: 0)
        .previewDevice(PreviewDevice(rawValue: "iPhone SE (3rd generation)"))
        .previewDisplayName("iPhone SE (3rd generation)")
        .previewLayout(.sizeThatFits)
      
      StoryRow(appState: appState, story: mockStory, index: 0)
        .previewDevice(PreviewDevice(rawValue: "iPhone SE (3rd generation)"))
        .previewDisplayName("iPhone SE (3rd generation), dark")
        .previewLayout(.sizeThatFits)
        .colorScheme(.dark)
      
      StoryRow(appState: appState, story: mockStory, index: 0)
        .previewDevice(PreviewDevice(rawValue: "iPad Pro (11-inch) (4th generation)"))
        .previewDisplayName("iPad Pro (11-inch) (4th generation)")
        .previewLayout(.sizeThatFits)
      
      StoryRow(appState: appState, story: mockStory, index: 0)
        .previewDevice(PreviewDevice(rawValue: "iPad Pro (11-inch) (4th generation)"))
        .previewDisplayName("iPad Pro (11-inch) (4th generation), dark")
        .previewLayout(.sizeThatFits)
        .colorScheme(.dark)
    }
  }
}
