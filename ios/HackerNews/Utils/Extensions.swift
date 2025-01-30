//
//  Extensions.swift
//  Hacker News
//
//  Created by Trevor Elkins on 8/15/23.
//

import Foundation
import SwiftSoup
import SwiftUI

extension String {
  func formattedHTML() -> AttributedString {
    // First convert common HTML tags to markdown
    var markdown =
      self
      .replacingOccurrences(of: "<p>", with: "\n\n")
      .replacingOccurrences(of: "</p>", with: "")
      .replacingOccurrences(of: "<i>", with: "*")  // Changed from _ to *
      .replacingOccurrences(of: "</i>", with: "*")  // Changed from _ to *
      .replacingOccurrences(of: "<em>", with: "*")  // Changed from _ to *
      .replacingOccurrences(of: "</em>", with: "*")  // Changed from _ to *
      .replacingOccurrences(of: "<b>", with: "**")
      .replacingOccurrences(of: "</b>", with: "**")
      .replacingOccurrences(of: "<strong>", with: "**")
      .replacingOccurrences(of: "</strong>", with: "**")
      .replacingOccurrences(of: "<pre><code>", with: "```\n")
      .replacingOccurrences(of: "</code></pre>", with: "\n```")
      .replacingOccurrences(of: "<code>", with: "`")
      .replacingOccurrences(of: "</code>", with: "`")
      .replacingOccurrences(of: "&gt;", with: ">")
      .replacingOccurrences(of: "&lt;", with: "<")
      .replacingOccurrences(of: "&amp;", with: "&")
      .replacingOccurrences(of: "&quot;", with: "\"")
      .replacingOccurrences(of: "&#x27;", with: "'")

    // Handle links - convert <a href="url">text</a> to [text](url)
    if let doc = try? SwiftSoup.parse(self) {
      if let links = try? doc.select("a") {
        for link in links {
          if let href = try? link.attr("href"),
            let text = try? link.text()
          {
            let htmlLink = try? link.outerHtml()
            let markdownLink = "[\(text)](\(href))"
            markdown = markdown.replacingOccurrences(
              of: htmlLink ?? "",
              with: markdownLink
            )
          }
        }
      }
    }

    // Convert to AttributedString
    return (try? AttributedString(markdown: markdown)) ?? AttributedString(self)
  }
}
