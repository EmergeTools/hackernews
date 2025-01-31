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
  // Basic HTML to Markdown conversion that seems to work decently well in testing
  // If we find bugs, maybe move to copying this solution: https://github.com/Dimillian/IceCubesApp/blob/main/Packages/Models/Sources/Models/Alias/HTMLString.swift
  func formattedHTML() -> AttributedString {
    var markdown =
      self
      .replacingOccurrences(of: "<p>", with: "\n")
      .replacingOccurrences(of: "</p>", with: "")
      .replacingOccurrences(of: "<br>", with: "\n")
      .replacingOccurrences(of: "<br/>", with: "\n")
      .replacingOccurrences(of: "<br />", with: "\n")
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
    do {
      let options = AttributedString.MarkdownParsingOptions(
        allowsExtendedAttributes: true,
        interpretedSyntax: .inlineOnlyPreservingWhitespace)
      return try AttributedString(markdown: markdown, options: options)
    } catch {
      return AttributedString(stringLiteral: self)
    }
  }
}
