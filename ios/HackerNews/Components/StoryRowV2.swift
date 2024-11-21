//
//  StoryRowV2.swift
//  HackerNews
//
//  Created by Rikin Marfatia on 11/20/24.
//

import SwiftUI

struct StoryRowV2: View {
    @ObservedObject var model: AppViewModel
    let story: Story
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            let author = story.by!
            Text("@\(author)")
                .foregroundColor(.hnOrange)
                .fontWeight(/*@START_MENU_TOKEN@*/.bold/*@END_MENU_TOKEN@*/)
            Text(story.title)
                .font(.headline)
            HStack(spacing: 16) {
                HStack {
                    Image(systemName: "arrow.up")
                        .foregroundColor(.green)
                    Text("\(story.score)")
                }
                HStack {
                    Image(systemName: "clock")
                        .foregroundColor(.purple)
                    Text(story.displayableDate)
                }
                Spacer()
                HStack {
                    Image(systemName: "bubble.right.fill")
                        .foregroundColor(.blue)
                    Text("\(story.commentCount)")
                }
                .padding(8)
                .background(.commentBackground)
                .cornerRadius(.infinity)
            }
        }
        .padding()
    }
}

struct StoryRowV2_Preview: PreviewProvider {
  static var previews: some View {
    let fakeStory = PreviewHelpers.makeFakeStory(index: 0, descendants: 3, kids: [1, 2, 3])
    PreviewVariants {
      StoryRowV2(model: AppViewModel(), story: fakeStory)
    }
  }
}
