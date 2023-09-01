//
//  PreviewingTest.swift
//  Hacker NewsUITests
//
//  Created by Trevor Elkins on 9/1/23.
//
import Foundation
import Snapshotting
import SnapshottingTests

class PreviewingTest: PreviewTest {

  override func getApp() -> XCUIApplication {
    return XCUIApplication()
  }

  override func snapshotPreviews() -> [String]? {
    return nil
  }
}
