//
//  HackerNewsSnapshotTest.swift
//  HackerNews
//
//  Created by Nicolas Hinderling on 10/9/24.
//

import Foundation
import SnapshottingTests
#if canImport(UIKit)
import UIKit
#endif
#if canImport(AccessibilitySnapshotCore)
import AccessibilitySnapshotCore
#endif

class HackerNewsSnapshotTest: SnapshotTest {
  override class func snapshotPreviews() -> [String]? {
    return nil
  }

  override class func excludedSnapshotPreviews() -> [String]? {
    return nil
  }
  
  override class func snapshotPreviewModules() -> [String]? {
    return nil
  }
  
  override class func excludedSnapshotPreviewModules() -> [String]? {
    return nil
  }
  
#if canImport(UIKit) && !os(watchOS) && !os(visionOS) && !os(tvOS)
// DNM diagnostic: a11y snapshotting disabled to isolate preview-count scaling.
// Returning nil skips the accessibility render path for every preview.
override open class func setupA11y() -> ((UIViewController, UIWindow, PreviewLayout) -> UIView)? {
  return nil
}
#endif
}

extension CGSize {
  var requiresCoreAnimationSnapshot: Bool {
    height >= UIScreen.main.bounds.size.height * 2
  }
}
