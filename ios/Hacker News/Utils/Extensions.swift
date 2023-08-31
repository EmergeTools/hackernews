//
//  Extensions.swift
//  Hacker News
//
//  Created by Trevor Elkins on 8/15/23.
//

import Foundation
import SwiftUI

extension PreviewProvider {
  static func withNavigationView(@ViewBuilder content: () -> some View) -> some View {
    NavigationView {
      ZStack {
        HNColors.background
          .edgesIgnoringSafeArea(.all)
        content()
          .toolbarColorScheme(.dark, for: .navigationBar)
          .toolbarBackground(HNColors.orange, for: .navigationBar)
          .toolbarBackground(.visible, for: .navigationBar)
      }
    }
  }
}

extension String {
  func htmlToAttributedString() -> NSAttributedString? {
    guard let data = self.data(using: .utf8) else { return nil }
    return try? NSAttributedString(
      data: data,
      options: [
        .documentType: NSAttributedString.DocumentType.html,
        .characterEncoding: String.Encoding.utf8.rawValue
      ],
      documentAttributes: nil)
  }
}
