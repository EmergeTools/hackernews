//
//  DateUtils.swift
//  Hacker News
//
//  Created by Ryan Brooks on 6/21/23.
//

import Foundation

extension Date {
    func timeAgoDisplay() -> String {
        let formatter = RelativeDateTimeFormatter()
        formatter.unitsStyle = .full
        return formatter.localizedString(for: self, relativeTo: Date())
    }
}
