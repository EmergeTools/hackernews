//
//  PostItemViewModel.swift
//  Hacker News
//
//  Created by Trevor Elkins on 8/24/23.
//

import Foundation

@MainActor
class StoryViewModel: ObservableObject {
  
  enum StoryState {
    case notStarted
    case loading
    case loaded(comments: [FlattenedComment])
  }
  
  @Published var story: Story
  @Published var state = StoryState.notStarted
  
  init(story: Story) {
    self.story = story
  }
  
  func fetchComments() async {
    state = .loading
    
    var commentsToRequest = story.comments
    var commentsById = [Int64 : Comment]()
    while !commentsToRequest.isEmpty {
      let items = await HNApi().fetchItems(ids: commentsToRequest)
      commentsToRequest.removeAll()
      for item in items {
        if let comment = item as? Comment {
          commentsById[comment.id] = comment
          commentsToRequest.append(contentsOf: comment.replies ?? [])
        } else {
          print("Found not comment \(item)")
        }
      }
    }
    
    var flattenedComments = [FlattenedComment]()
    flattenComments(
      ids: story.comments,
      flattened: &flattenedComments,
      commentsById: &commentsById
    )
    
    state = .loaded(comments: flattenedComments)
  }
  
  private func flattenComments(ids: [Int64], depth: Int = 0, flattened: inout [FlattenedComment], commentsById: inout [Int64 : Comment]) {
    for id in ids {
      if let foundComment = commentsById[id] {
        flattened.append(FlattenedComment(comment: foundComment, depth: depth))
        flattenComments(ids: foundComment.replies ?? [], depth: depth + 1, flattened: &flattened, commentsById: &commentsById)
      } else {
        print("Could not find comment \(id)")
      }
    }
  }
}

struct FlattenedComment: Identifiable {
  let comment: Comment
  let depth: Int
  var id: Int64 {
    comment.id
  }
}
