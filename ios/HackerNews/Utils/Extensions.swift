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
    var markdown =
      self
      .replacingOccurrences(of: "<p>", with: "\n")
      .replacingOccurrences(of: "</p>", with: "\n")
      .replacingOccurrences(of: "<i>", with: "*")
      .replacingOccurrences(of: "</i>", with: "*")
      .replacingOccurrences(of: "&gt;", with: ">")
      .replacingOccurrences(of: "&lt;", with: "<")
      .replacingOccurrences(of: "&amp;", with: "&")
      .replacingOccurrences(of: "&quot;", with: "\"")
      .replacingOccurrences(of: "&#x27;", with: "'")
      .replacingOccurrences(of: "<pre><code>", with: "```\n")
      .replacingOccurrences(of: "</code></pre>", with: "\n```")

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

    return (try? AttributedString(markdown: markdown)) ?? AttributedString(self)
  }
}
