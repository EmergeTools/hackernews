//
//  OrderFileTests.swift
//  HackerNews
//
//  Created by Trevor Elkins on 5/28/25.
//

import FaultOrderingTests
import XCTest

final class OrderFileTests: XCTestCase {

  override func setUpWithError() throws {
    continueAfterFailure = false
  }

  @MainActor func testLaunch() throws {
    let app = XCUIApplication()

    let test = FaultOrderingTest { app in
      
    }

    test.testApp(testCase: self, app: app)
  }
}
