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
  let commentForm: CommentFormData?
}

struct CommentInfo {
  let id: Int64
  var upvoted: Bool
  let upvoteUrl: String
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

struct CommentFormData {
  let parentId: String
  let gotoUrl: String
  let hmac: String
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
      let document = try SwiftSoup.parse(html)
      let postInfo = try document.postInfo(id: id)
      let comments = try document.comments()
      let commentFormData = try document.commentFormData()

      return .success(data: PostPageResponse(
        postInfo: postInfo,
        comments: comments,
        commentForm: commentFormData
      ))
    } catch {
      print("Error fetching post IDs: \(error)")
      return .error
    }
  }

  @discardableResult
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

  func postComment(
    parentId: String,
    gotoUrl: String,
    hmac: String,
    text: String
  ) async -> [CommentInfo] {
    let url = URL(string: COMMENT_URL)!
    var request = URLRequest(url: url)
    var urlComponent = URLComponents()
    urlComponent.queryItems = [
      URLQueryItem(name: "parent", value: parentId),
      URLQueryItem(name: "goto", value: gotoUrl),
      URLQueryItem(name: "hmac", value: hmac),
      URLQueryItem(name: "text", value: text),
    ]
    let formString = urlComponent.string!.dropFirst() // remove the ?, easy way to encode string
    print("Form Data: ", formString)
    request.httpMethod = "POST"
    request.httpBody = formString.data(using: .utf8)
    do {
      let (data, _) = try await session.data(for: request)
      guard let html = String(data: data, encoding: .utf8) else { return [] }
      let document = try SwiftSoup.parse(html)
      let comments = try document.comments()
      return comments
    } catch {
      return []
    }
  }
}

private extension Document {
  func postInfo(id: Int64) throws -> PostInfo {
    let postUpvoteLinkElement = try self.select("#up_\(id)").first()
    let upvoteUrl = try postUpvoteLinkElement?.attr("href") ?? ""
    let upvoted = postUpvoteLinkElement?.hasClass("nosee") ?? false

    return PostInfo(
      id: id,
      upvoted: upvoted,
      upvoteUrl: !upvoteUrl.isEmpty ? BASE_WEB_URL + upvoteUrl : ""
    )
  }

  func comments() throws -> [CommentInfo] {
    let commentTree = try self.select("table.comment-tree tr.athing.comtr")
    let comments: [CommentInfo] = try commentTree.map { comment in
      let commentId = try Int64(comment.id(), format: .number)
      let commentLevel = try comment.select("td.ind").attr("indent")
      let commentText = try comment.select("div.commtext").text()
      let commentAuthor = try comment.select("a.hnuser").text()
      let commentDate = try comment.select("span.age").attr("title").split(separator: " ").first!
      let upvoteLinkElement = try comment.select("a[id^=up_").first()
      let upvoteUrl = try upvoteLinkElement?.attr("href") ?? ""
      let upvoted = upvoteLinkElement?.hasClass("nosee") ?? false
      let date = String(commentDate).asDate()

      return CommentInfo(
        id: commentId,
        upvoted: upvoted,
        upvoteUrl: !upvoteUrl.isEmpty ? BASE_WEB_URL + upvoteUrl : "",
        text: commentText,
        user: commentAuthor,
        age: date?.timeAgoDisplay() ?? "",
        level: Int(commentLevel)!
      )
    }
    return comments
  }

  func commentFormData() throws -> CommentFormData? {
    let formElement = try self.select("form[action=comment]")
    if (formElement.isEmpty()) { return nil }

    let parentId = try formElement.select("input[name=parent]").attr("value")
    let goto = try formElement.select("input[name=goto]").attr("value")
    let hmac = try formElement.select("input[name=hmac]").attr("value")

    return CommentFormData(parentId: parentId, gotoUrl: goto, hmac: hmac)
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
