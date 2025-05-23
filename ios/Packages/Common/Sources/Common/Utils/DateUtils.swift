//
//  DateUtils.swift
//  Hacker News
//
//  Created by Ryan Brooks on 6/21/23.
//

import Foundation

extension Date {
  public func timeAgoDisplay() -> String {
    let formatter = RelativeDateTimeFormatter()
    formatter.unitsStyle = .full
    return formatter.localizedString(for: self, relativeTo: Date())
  }
}

extension String {
  /**
   Handle various time strings that we can get from the HN API / Web
   2024-12-27T13:59:29
   2024-09-05T17:48:25.000000Z
   */
  public func asDate() -> Date? {
    let regularFormatter = DateFormatter()
    regularFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss"
    regularFormatter.timeZone = TimeZone(identifier: "UTC")

    let secondaryFormatter = ISO8601DateFormatter()
    secondaryFormatter.formatOptions = [.withInternetDateTime, .withFractionalSeconds]
    secondaryFormatter.timeZone = TimeZone(identifier: "UTC")

    let date = regularFormatter.date(from: self) ?? secondaryFormatter.date(from: self)
    return date
  }
}
