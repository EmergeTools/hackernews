//
//  PostItemViewModel.swift
//  Hacker News
//
//  Created by Trevor Elkins on 8/24/23.
//

import Foundation

struct StoryUiState {
  var headerState: CommentsHeaderState
  var comments: CommentsState
}

enum CommentsState {
  case notStarted
  case loading
  case loaded(comments: [CommentInfo])
}

struct CommentsHeaderState {
  let story: Story
  var expanded: Bool = false
}

@MainActor
class StoryViewModel: ObservableObject {

  @Published var state: StoryUiState

  private let story: Story

  private let webClient = HNWebClient()
  private let cookieStorage = HTTPCookieStorage.shared

  init(story: Story) {
    self.story = story
    self.state = StoryUiState(
      headerState: CommentsHeaderState(story: story),
      comments: .notStarted
    )
  }

  func toggleHeaderBody() {
    state.headerState.expanded.toggle()
  }


  func fetchComments() async {
    state.comments = .loading
    let page = await webClient.getStoryPage(id: story.id)
    switch page {
    case .success(let data):
      state.comments = .loaded(comments: data.comments)
    case .error:
      state.comments = .loaded(comments: [])
    }
  }

  private func isLoggedIn() -> Bool {
    return cookieStorage.cookies?.isEmpty == false
  }

  func likeComment(comment: CommentInfo) async {
    print("DEBUG: like comment clicked for \(comment.id)")
    if (isLoggedIn()) {
      let success = await webClient.upvoteItem(upvoteUrl: comment.upvoteUrl!)
      print("DEBUG: upvoted comment \(success)")
    } else {
      // navigate to login modal
    }
  }
}
