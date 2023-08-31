//
//  PostItemViewModel.swift
//  Hacker News
//
//  Created by Trevor Elkins on 8/24/23.
//

import Foundation

@MainActor
class StoryViewModel: ObservableObject {
  
  @Published var story: Story
  @Published var comments: [Comment] = []
  @Published var isLoadingComments = false
  
  init(story: Story) {
    self.story = story
  }
  
  func fetchComments() async {
    isLoadingComments = true
    let items = await HNApi().fetchItems(ids: story.comments)
    var comments = [Comment]()
    for item in items {
      if let comment = item as? Comment {
        comments.append(comment)
      } else {
        print("Not comment \(item)")
      }
    }
    self.comments = comments
    isLoadingComments = false
  }
  
  private func fetchComments(commentIDs: [Int]) {
//    for id in commentIDs {
//      HackerNewsAPI.fetchItem(itemID: id) { comment in
//        DispatchQueue.main.async {
//          if let comment = comment {
//            self.comments.append(comment)
//            
//            if let kids = comment.kids {
//              self.fetchComments(commentIDs: kids)
//            }
//          }
//        }
//      }
//    }
  }
}
