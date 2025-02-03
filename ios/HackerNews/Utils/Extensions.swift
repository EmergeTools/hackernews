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
    var processedText = self

    // Handle links separately via parsing the HTML
    if let doc = try? SwiftSoup.parse(processedText) {
      if let links = try? doc.select("a") {
        for link in links {
          if let href = try? link.attr("href"),
            let text = try? link.text(),
            let htmlLink = try? link.outerHtml()
          {
            processedText = processedText.replacingOccurrences(
              of: htmlLink, with: "[\(text)](\(href))")
          }
        }
      }
    }

    processedText =
      processedText
      .replacingOccurrences(of: "&amp;", with: "&")
      .replacingOccurrences(of: "&gt;", with: ">")
      .replacingOccurrences(of: "&lt;", with: "<")
      .replacingOccurrences(of: "&quot;", with: "\"")
      .replacingOccurrences(of: "&#x27;", with: "'")
      .replacingOccurrences(of: "<p>", with: "\n")
      .replacingOccurrences(of: "</p>", with: "\n")
      .replacingOccurrences(of: "<br>", with: "\n")
      .replacingOccurrences(of: "<br/>", with: "\n")
      .replacingOccurrences(of: "<br />", with: "\n")
      .replacingOccurrences(of: "<i>", with: "*")
      .replacingOccurrences(of: "</i>", with: "*")
      .replacingOccurrences(of: "<pre><code>", with: "```\n")
      .replacingOccurrences(of: "</code></pre>", with: "\n```")

    if processedText.hasPrefix("\n") {
      processedText.removeFirst()
    }
    if processedText.hasSuffix("\n") {
      processedText.removeLast()
    }

    do {
      let options = AttributedString.MarkdownParsingOptions(
        allowsExtendedAttributes: true,
        interpretedSyntax: .inlineOnlyPreservingWhitespace)
      return try AttributedString(markdown: processedText, options: options)
    } catch {
      return AttributedString(stringLiteral: self)
    }
  }
}
