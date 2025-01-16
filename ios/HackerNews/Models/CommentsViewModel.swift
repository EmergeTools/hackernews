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
  case loaded(comments: [CommentState])
}

struct CommentState {
  let id: Int64
  var upvoted: Bool
  let upvoteUrl: String
  let text: String
  let user: String
  let age: String
  let level: Int
  var hidden: Bool
}

extension CommentInfo {
  func toCommentState() -> CommentState {
    return CommentState(
      id: self.id,
      upvoted: self.upvoted,
      upvoteUrl: self.upvoteUrl,
      text: self.text,
      user: self.user,
      age: self.age,
      level: self.level,
      hidden: false
    )
  }
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

  func fetchPage() async {
    state.comments = .loading
    let page = await webClient.getStoryPage(id: story.id)
    switch page {
    case .success(let data):
      state.headerState.upvoted = data.postInfo.upvoted
      state.headerState.upvoteLink = data.postInfo.upvoteUrl
      state.comments = .loaded(comments: data.comments.map { $0.toCommentState() })
    case .error:
      state.comments = .loaded(comments: [])
    }
  }

  private func isLoggedIn() -> Bool {
    return cookieStorage.cookies?.isEmpty == false
  }

  func goBack() {
    path.wrappedValue.removeLast()
  }

  func likePost(upvoted: Bool, url: String) async {
    if (isLoggedIn()) {
      print("Like Post: \(url)")
      guard !url.isEmpty || upvoted else { return }
      state.headerState.upvoted = true
      await webClient.upvoteItem(upvoteUrl: url)
    } else {
      // navigate to login modal
    }
  }

  func likeComment(data: CommentState) async {
    if (isLoggedIn()) {
      guard case .loaded(let comments) = state.comments else { return }
      guard !data.upvoteUrl.isEmpty || data.upvoted else { return }
      var updated = data
      updated.upvoted = true
      state.comments = .loaded(comments: comments.map { comment in
        if (data.id == comment.id) {
          updated
        } else {
          comment
        }
      })
      print("Like Comment: \(data.upvoteUrl)")
      await webClient.upvoteItem(upvoteUrl: data.upvoteUrl)
    } else {
      // navigate to login modal
    }
  }

  func toggleHeaderBody() {
    state.headerState.expanded.toggle()
  }

  func toggleComment(commentId: Int64) {
    if case .loaded(let comments) = state.comments {
      var updates: [CommentState] = []

      let parentIndex = comments.firstIndex { $0.id == commentId }!
      var parent = comments[parentIndex]
      parent.hidden.toggle()
      updates.append(parent)

      let parentLevel = parent.level
      var currentIndex = parentIndex+1
      while currentIndex < comments.count {
        var child = comments[currentIndex]
        if (child.level <= parentLevel) {
          break
        }
        child.hidden = parent.hidden
        updates.append(child)
        currentIndex += 1
      }

      state.comments = .loaded(
        comments: comments.map { old in
          updates.first { $0.id == old.id } ?? old
        }
      )
    }
  }
}
