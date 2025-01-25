//
//  Extensions.swift
//  Hacker News
//
//  Created by Trevor Elkins on 8/15/23.
//

import Foundation
import SwiftSoup
import UIKit

/// This code provides a workaround to enable the interactive pop gesture in NavigationStacks in SwiftUI.
/// While not the most elegant or ideal solution, it significantly enhances the navigation user experience.
///
/// Note: This is intended as a temporary fix and should be removed once Apple adds proper support
/// for interactive pop gestures in NavigationStacks. Please refer to PR #327 for more details.
extension UINavigationController: @retroactive UIGestureRecognizerDelegate {
  override open func viewDidLoad() {
    super.viewDidLoad()
    interactivePopGestureRecognizer?.delegate = self
  }

  public func gestureRecognizerShouldBegin(_ gestureRecognizer: UIGestureRecognizer) -> Bool {
    return viewControllers.count > 1
  }
}

extension String {
  func strippingHTML() -> String {
    guard let doc: Document = try? SwiftSoup.parse(self) else { return "" } // parse html
    return (try? doc.text()) ?? ""
  }
}
