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
  let postInfo: PostInfo
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

struct PostInfo {
  let id: Int64
  let upvoted: Bool
  let upvoteUrl: String
}

struct LoginBody: Codable {
  let acct: String
  let pw: String
}

class HNWebClient {
  private let session = URLSession.shared

  func login(acct: String, pw: String) async -> LoginStatus {
    let url = URL(string: LOGIN_URL)!
    var request = URLRequest(url: url)
    let formData = ["acct": acct, "pw": pw]
    let formString = formData.map { key, value in
          let escapedKey = key.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) ?? key
          let escapedValue = value.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) ?? value
          return "\(escapedKey)=\(escapedValue)"
      }.joined(separator: "&")
    request.httpMethod = "POST"
    request.httpBody = formString.data(using: .utf8)

    do {
      let (data, _) = try await session.data(for: request)
      let html = String(data: data, encoding: .utf8)!
      print("Login HTML: ", html)
      let document = try SwiftSoup.parse(html)
      let failed = try document.select("b").first()?.text() == "Login"
      return failed ? .error : .success
    } catch {
      return .error
    }
  }

  func getStoryPage(id: Int64) async -> PostPage {
    // make request for page
    let url = URL(string:"\(ITEM_URL)?id=\(id)")!
    let request = URLRequest(url: url)
    do {
      let (data, _) = try await session.data(for: request)
      guard let html = String(data: data, encoding: .utf8) else { return .error }
      let document: Document = try SwiftSoup.parse(html)
      // Post Info
      let postUpvoteLinkElement = try document.select("#up_\(id)").first()
      let upvoteUrl = try postUpvoteLinkElement?.attr("href") ?? ""
      let upvoted = postUpvoteLinkElement?.hasClass("nosee") ?? false
      let postInfo = PostInfo(
        id: id,
        upvoted: upvoted,
        upvoteUrl: !upvoteUrl.isEmpty ? BASE_WEB_URL + upvoteUrl : ""
      )

      // Comment Info
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
      return .success(data: PostPageResponse(postInfo: postInfo, comments: comments))
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

extension HTTPCookieStorage {
  func removeCookies() {
    self.removeCookies(since: Date(timeIntervalSince1970: 0))
  }
}
