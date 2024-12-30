//
//  HNWebClient.swift
//  HackerNews
//
//  Created by Rikin Marfatia on 12/26/24.
//

import Foundation
import SwiftSoup

let BASE_WEB_URL = "https://news.ycombinator.com/"
private let LOGIN_URL = BASE_WEB_URL + "login"
private let ITEM_URL = BASE_WEB_URL + "item"
private let COMMENT_URL = BASE_WEB_URL + "comment"

enum PostPage {
  case success(data: PostPageResponse)
  case error
}

struct PostPageResponse {
  let comments: [CommentInfo]
}

struct CommentInfo {
  let id: Int64
  let upvoted: Bool
  let upvoteUrl: String
  let text: String
  let user: String
  let age: String
  let level: Int
}

class HNWebClient {
  func getStoryPage(id: Int64) async -> PostPage {
    // make request for page
    let url = URL(string:"\(ITEM_URL)?id=\(id)")
    do {
      let (data, _) = try await URLSession.shared.data(from: url!)
      guard let html = String(data: data, encoding: .utf8) else { return .error }
      let document: Document = try SwiftSoup.parse(html)
      let commentTree = try document.select("table.comment-tree tr.athing.comtr")
      let comments: [CommentInfo] = try commentTree.map { comment in
        // get the comment text
        let commentId = try Int64(comment.id(), format: .number)
        let commentLevel = try comment.select("td.ind").attr("indent")
        let commentText = try comment.select("div.commtext").text()
        let commentAuthor = try comment.select("a.hnuser").text()
        let commentDate = try comment.select("span.age").attr("title").split(separator: " ").first!
        let upvoteLinkElement = try comment.select("a[id^=up_").first()!
        let upvoteUrl = try upvoteLinkElement.attr("href")
        let upvoted = upvoteLinkElement.hasClass("nosee")

        //2024-12-27T13:59:29 or 2024-09-05T17:48:25.000000Z
        let date = String(commentDate).asDate()

        return CommentInfo(
          id: commentId,
          upvoted: upvoted,
          upvoteUrl: upvoteUrl,
          text: commentText,
          user: commentAuthor,
          age: date?.timeAgoDisplay() ?? "",
          level: Int(commentLevel)!
        )
      }
      return .success(data: PostPageResponse(comments: comments))
    } catch {
      print("Error fetching post IDs: \(error)")
      return .error
    }
  }
}
