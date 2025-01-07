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
  let upvoteUrl: String?
  let text: String
  let user: String
  let age: String
  let level: Int
}

struct LoginBody: Codable {
  let acct: String
  let pw: String
}

class HNWebClient {
  private let session = URLSession.shared

  func login(with: LoginBody) async throws -> (Data, URLResponse) {
    let url = URL(string: LOGIN_URL)!
    var request = URLRequest(url: url)
    let formData = ["acct": with.acct, "pw": with.pw]
    let formString = formData.map { key, value in
          let escapedKey = key.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) ?? key
          let escapedValue = value.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) ?? value
          return "\(escapedKey)=\(escapedValue)"
      }.joined(separator: "&")
    request.httpMethod = "POST"
    request.httpBody = formString.data(using: .utf8)

    return try await session.data(for: request)
  }

  func getStoryPage(id: Int64) async -> PostPage {
    // make request for page
    let url = URL(string:"\(ITEM_URL)?id=\(id)")!
    let request = URLRequest(url: url)
    do {
      let (data, _) = try await session.data(for: request)
      guard let html = String(data: data, encoding: .utf8) else { return .error }
      let document: Document = try SwiftSoup.parse(html)
      let commentTree = try document.select("table.comment-tree tr.athing.comtr")
      let comments: [CommentInfo] = try commentTree.map { comment in
        let commentId = try Int64(comment.id(), format: .number)
        let commentLevel = try comment.select("td.ind").attr("indent")
        let commentText = try comment.select("div.commtext").text()
        let commentAuthor = try comment.select("a.hnuser").text()
        let commentDate = try comment.select("span.age").attr("title").split(separator: " ").first!
        let upvoteLinkElement = try comment.select("a[id^=up_").first()
        let upvoteUrl = try upvoteLinkElement?.attr("href")
        let upvoted = upvoteLinkElement?.hasClass("nosee") ?? false

        print("DEBUG: upvoteUrl: \(upvoteUrl ?? "")")

        let date = String(commentDate).asDate()

        return CommentInfo(
          id: commentId,
          upvoted: upvoted,
          upvoteUrl: upvoteUrl != nil ? BASE_WEB_URL + upvoteUrl! : nil,
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

  func upvoteItem(upvoteUrl: String) async -> Bool {
    let url = URL(string: upvoteUrl)!
    do {
      let (_, response) = try await session.data(from: url)
      let httpResponse = response as! HTTPURLResponse
      return httpResponse.isSuccessful()
    } catch {
      return false
    }
  }
}

extension HTTPURLResponse {
  func isSuccessful() -> Bool {
    return statusCode >= 200 && statusCode < 300
  }
}
