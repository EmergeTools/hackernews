//
//  Emerge_Perf_Tests.swift
//  Emerge Perf Tests
//
//  Created by Trevor Elkins on 10/11/23.
//

import XCTest

final class EmergePerfTest_TapsOnPostRow: XCTestCase, EMGPerfTest {
  func runInitialSetup(withApp app: XCUIElement) {}
  
  func runIteration(withApp app: XCUIElement) {
    let loginButton = app.buttons["Login"]
    loginButton.tap()
    
    let firstRow = app.cells.element(boundBy: 0)
    let exists = NSPredicate(format: "exists == true")
    
    expectation(for: exists, evaluatedWith: firstRow, handler: nil)
    waitForExpectations(timeout: 5, handler: nil)
    
    XCTAssertTrue(firstRow.exists, "First row does not exist")
    firstRow.tap()
  }
}
