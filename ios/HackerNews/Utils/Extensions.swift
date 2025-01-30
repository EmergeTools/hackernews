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
      .replacingOccurrences(of: "<i>", with: "_")
      .replacingOccurrences(of: "</i>", with: "_")
      .replacingOccurrences(of: "<em>", with: "_")
      .replacingOccurrences(of: "</em>", with: "_")
      .replacingOccurrences(of: "<b>", with: "**")
      .replacingOccurrences(of: "</b>", with: "**")
      .replacingOccurrences(of: "<strong>", with: "**")
      .replacingOccurrences(of: "</strong>", with: "**")
    
    print("telkins")

    // Handle links - convert <a href="url">text</a> to [text](url)
    if let doc = try? SwiftSoup.parse(self) {
      if let links = try? doc.select("a") {
        for link in links {
          if let href = try? link.attr("href"),
            let text = try? link.text()
          {
            let htmlLink = try? link.outerHtml()
            let markdownLink = "[\(text)](\(href))"
            print("Link replacement:")
            print("  Original HTML: \(htmlLink ?? "")")
            print("  Text: \(text)")
            print("  URL: \(href)")
            print("  Markdown: \(markdownLink)")

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
