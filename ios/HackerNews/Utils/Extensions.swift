//
//  Extensions.swift
//  Hacker News
//
//  Created by Trevor Elkins on 8/15/23.
//

import Foundation
import SwiftSoup

extension String {
  func strippingHTML() -> String {
    guard let doc: Document = try? SwiftSoup.parse(self) else { return "" } // parse html
    return (try? doc.text()) ?? ""
  }
}
