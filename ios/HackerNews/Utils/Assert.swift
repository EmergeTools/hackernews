//
//  Assert.swift
//  HackerNews
//
//  Created by Trevor Elkins on 4/21/25.
//

import Foundation
import SentrySwift

/// Fails fast in Debug, logs to Sentry in Release.
/// - Parameters:
///   - condition: Pass‑through closure so the boolean is only evaluated once.
///   - message:   Human‑readable failure description.
///   - file / line / function: Auto‑filled call‑site metadata.
@inline(__always)
public func et_assertionFailure(
  _ message: @autoclosure () -> String,
  file: StaticString = #fileID,
  line: UInt = #line,
  function: StaticString = #function
) {
  #if DEBUG
    fatalError("\(message()) — \(function) @ \(file):\(line)")
  #else
  let scope = SentrySwift.Scope()
  scope.setTag(value: "assertion", key: "type")
  let sentryId = SentrySDK.capture(message: message(), scope: scope)
  print("Created Sentry event \(sentryId)")
  #endif
}
