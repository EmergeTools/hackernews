//
//  CommentsViewModel.swift
//  Hacker News
//
//  Created by Trevor Elkins on 8/24/23.
//

import Foundation
import SwiftUI

struct CommentsUiState {
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
  var upvoteLink: String = ""
  var upvoted: Bool = false
}

@MainActor
class CommentsViewModel: ObservableObject {

  @Published var state: CommentsUiState

  private let story: Story
  private var path: Binding<NavigationPath>

  private let webClient = HNWebClient()
  private let cookieStorage = HTTPCookieStorage.shared

  init(story: Story, path: Binding<NavigationPath>) {
    self.story = story
    self.path = path
    self.state = CommentsUiState(
      headerState: CommentsHeaderState(story: story),
      comments: .notStarted
    )
  }

  func toggleHeaderBody() {
    state.headerState.expanded.toggle()
  }

  func fetchPage() async {
    state.comments = .loading
    let page = await webClient.getStoryPage(id: story.id)
    switch page {
    case .success(let data):
      state.headerState.upvoted = data.postInfo.upvoted
      state.headerState.upvoteLink = data.postInfo.upvoteUrl
      state.comments = .loaded(comments: data.comments)
    case .error:
      state.comments = .loaded(comments: [])
    }
  }

  func goBack() {
    path.wrappedValue.removeLast()
  }

  private func isLoggedIn() -> Bool {
    return cookieStorage.cookies?.isEmpty == false
  }

  func likePost(upvoted: Bool, url: String) async {
    if (isLoggedIn()) {
      print("Like Post: \(url)")
      guard !url.isEmpty || upvoted else { return }
      state.headerState.upvoted = true
      let success = await webClient.upvoteItem(upvoteUrl: url)
      if !success {
        state.headerState.upvoted = false
      }
    } else {
      // navigate to login modal
    }
  }

  func likeComment(comment: CommentInfo) async {
    if (isLoggedIn()) {
      print("Like Comment: \(comment.upvoteUrl ?? "")")
      let success = await webClient.upvoteItem(upvoteUrl: comment.upvoteUrl!)
    } else {
      // navigate to login modal
    }
  }
}
