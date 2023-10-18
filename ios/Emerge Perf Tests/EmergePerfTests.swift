//
//  Emerge_Perf_Tests.swift
//  Emerge Perf Tests
//
//  Created by Trevor Elkins on 10/11/23.
//

import XCTest

final class EmergePerfTest_TapsOnPostRow: NSObject, EMGPerfTest {
  func runInitialSetup(withApp app: XCUIElement) {}
  
  func runIteration(withApp app: XCUIElement) {
    let loginButton = app.buttons["Login"]
    loginButton.tap()
    
    let firstRow = app.cells.element(boundBy: 0)
    firstRow.tap()
  }
}
