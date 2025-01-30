//
//  Flags.swift
//  Hacker News
//
//  Created by Trevor Elkins on 3/21/24.
//

import Foundation

struct Flags {
  enum Flag {
    case networkDebugger
  }
  static func isEnabled(_ flag: Flag) -> Bool {
    return false
  }
}
