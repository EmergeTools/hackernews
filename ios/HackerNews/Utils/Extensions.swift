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
    // Unescape HTML entities in the entire string before handling links
    var processedText =
      self
      .replacingOccurrences(of: "&amp;", with: "&")
      .replacingOccurrences(of: "&gt;", with: ">")
      .replacingOccurrences(of: "&lt;", with: "<")
      .replacingOccurrences(of: "&quot;", with: "\"")
      .replacingOccurrences(of: "&#x27;", with: "'")

    // Handle links using regex
    // Note: I tried using SwiftSoup at first, but it was having difficulty with edge cases
    let linkPattern = #"<a[^>]*href="([^"]*)"[^>]*>(.*?)</a>"#
    if let regex = try? NSRegularExpression(pattern: linkPattern) {
      let range = NSRange(processedText.startIndex..<processedText.endIndex, in: processedText)
      processedText = regex.stringByReplacingMatches(
        in: processedText,
        range: range,
        withTemplate: "[$2]($1)"
      )
    }

    processedText =
      processedText
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
