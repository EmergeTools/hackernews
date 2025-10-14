//
//  CommentsViewModel.swift
//  Hacker News
//
//  Created by Trevor Elkins on 8/24/23.
//

import Foundation
import SwiftUI
import Common

struct CommentsUiState {
  var headerState: CommentsHeaderState
  var comments: CommentsState
  let auth: AuthState
  var postCommentState: CommentComposerState? = nil
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
  let hideUrl: String
  let flagUrl: String
  let text: String
  let user: String
  let age: String
  let level: Int
  var hidden: Bool
}

extension Array where Element == CommentState {
  func shouldHide(id: Int64) -> Bool {
    guard let idx = firstIndex(where: { $0.id == id }) else { return false }
    
    var level = self[idx].level
  
    for ancestor in self[..<idx].reversed() {
      if ancestor.level < level {
        if ancestor.hidden { return true }
        level = ancestor.level
        if level == 0 { break }
      }
    }
    return false
  }
}

extension CommentInfo {
  func toCommentState() -> CommentState {
    return CommentState(
      id: self.id,
      upvoted: self.upvoted,
      upvoteUrl: self.upvoteUrl,
      hideUrl: self.hideUrl,
      flagUrl: self.flagUrl,
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
  var hideUrl: String = ""
  var flagUrl: String = ""
}

struct CommentComposerState {
  let parentId: String
  let goToUrl: String
  let hmac: String
  var loggedIn: AuthState
  var text: String
}

extension CommentFormData {
  func toCommentComposerState(auth: AuthState) -> CommentComposerState {
    return CommentComposerState(
      parentId: self.parentId,
      goToUrl: self.gotoUrl,
      hmac: self.hmac,
      loggedIn: auth,
      text: ""
    )
  }
}

enum CommentsDestination {
  case back
  case login
  case website(url: URL)
}

@MainActor
@Observable
class CommentsViewModel {
  
  static let longConstant = "THIS_IS_A_REALLY_LONG_LITERAL_THIS_IS_A_REALLY_LONG_LITERAL_THIS_IS_A_REALLY_LONG_LITERAL_THIS_IS_A_REALLY_LONG_LITERAL_THIS_IS_A_REALLY_LONG_LITERAL_THIS_IS_A_REALLY_LONG_LITERAL_THIS_IS_A_REALLY_LONG_LITERAL_THIS_IS_A_REALLY_LONG_LITERAL_THIS_IS_A_REALLY_LONG_LITERAL_THIS_IS_A_REALLY_LONG_LITERAL_THIS_IS_A_REALLY_LONG_LITERAL_THIS_IS_A_REALLY_LONG_LITERAL_THIS_IS_A_REALLY_LONG_LITERAL_THIS_IS_A_REALLY_LONG_LITERAL_THIS_IS_A_REALLY_LONG_LITERAL_THIS_IS_A_REALLY_LONG_LITERAL_THIS_IS_A_REALLY_LONG_LITERAL_THIS_IS_A_REALLY_LONG_LITERAL_THIS_IS_A_REALLY_LONG_LITERAL_THIS_IS_A_REALLY_LONG_LITERAL_THIS_IS_A_REALLY_LONG_LITERAL_THIS_IS_A_REALLY_LONG_LITERAL_THIS_IS_A_REALLY_LONG_LITERAL_THIS_IS_A_REALLY_LONG_LITERAL_THIS_IS_A_REALLY_LONG_LITERAL_THIS_IS_A_REALLY_LONG_LITERAL_THIS_IS_A_REALLY_LONG_LITERAL_THIS_IS_A_REALLY_LONG_LITERAL_THIS_IS_A_REALLY_LONG_LITERAL_THIS_IS_A_REALLY_LONG_LITERAL_THIS_IS_A_REALLY_LONG_LITERAL_THIS_IS_A_REALLY_LONG_LITERAL_THIS_IS_A_REALLY_LONG_LITERAL_THIS_IS_A_REALLY_LONG_LITERAL_THIS_IS_A_REALLY_LONG_LITERAL_THIS_IS_A_REALLY_LONG_LITERAL_THIS_IS_A_REALLY_LONG_LITERAL"

  var state: CommentsUiState

  private let story: Story
  private let navigation: (_ to: CommentsDestination) -> Void

  private let webClient = HNWebClient()
  private let cookieStorage = HTTPCookieStorage.shared

  init(story: Story, auth: AuthState, navigation: @escaping (CommentsDestination) -> Void) {
    self.story = story
    self.navigation = navigation
    self.state = CommentsUiState(
      headerState: CommentsHeaderState(story: story),
      comments: .notStarted,
      auth: auth
    )

    loadInitalPage()
  }

  private func loadInitalPage() {
    Task {
      await fetchPage()
    }
  }

  func fetchPage() async {
    Logger.info("Fetching comments")
    state.comments = .loading
    let page = await webClient.getStoryPage(id: story.id)
    switch page {
    case .success(let data):
      state.headerState.upvoted = data.postInfo.upvoted
      state.headerState.upvoteLink = data.postInfo.upvoteUrl
      state.headerState.hideUrl = data.postInfo.hideUrl
      state.headerState.flagUrl = data.postInfo.flagUrl
      state.comments = .loaded(comments: data.comments.map { $0.toCommentState() })
      state.postCommentState = data.commentForm?.toCommentComposerState(auth: state.auth)
    case .error:
      state.comments = .loaded(comments: [])
    }
  }

  func goBack() {
    navigation(.back)
  }

  func likePost(upvoted: Bool, url: String) async {
    switch state.auth {
    case .loggedIn:
      Logger.info("Like Post: \(url)")
      guard !url.isEmpty || upvoted else { return }
      state.headerState.upvoted = true
      await webClient.upvoteItem(upvoteUrl: url)
    case .loggedOut:
      navigation(.login)
    }
  }

  func likeComment(data: CommentState) async {
    switch state.auth {
    case .loggedIn:
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
      Logger.info("Like Comment: \(data.upvoteUrl)")
      await webClient.upvoteItem(upvoteUrl: data.upvoteUrl)
    case .loggedOut:
      navigation(.login)
    }
  }
  
  func flagPost(url: String) async {
    switch state.auth {
    case .loggedIn:
      guard !url.isEmpty else { return }
      await webClient.flagItem(flagUrl: url)
    case .loggedOut:
      navigation(.login)
    }
  }
  
  func flagComment(data: CommentState) async {
    switch state.auth {
    case .loggedIn:
      guard !data.flagUrl.isEmpty else { return }
      await webClient.flagItem(flagUrl: data.flagUrl)
    case .loggedOut:
      navigation(.login)
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

  func goToLogin() {
    navigation(.login)
  }
  
  func goToWebsite() {
    guard let url = story.makeUrl() else { return }
    navigation(.website(url: url))
  }

  func updateComment(text: String) {
    state.postCommentState?.text = text
  }

  func sendComment() async {
    if (state.postCommentState != nil) {
      let parent = state.postCommentState!.parentId
      let hmac = state.postCommentState!.hmac
      let goto = state.postCommentState!.goToUrl
      let text = state.postCommentState!.text
      state.postCommentState?.text = ""

      let updated = await webClient.postComment(parentId: parent, gotoUrl: goto, hmac: hmac, text: text)
      if (!updated.isEmpty) {
        state.comments = .loaded(comments: updated.map { $0.toCommentState() })
      }
    }
  }
}
