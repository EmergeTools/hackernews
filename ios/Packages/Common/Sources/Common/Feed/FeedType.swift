//
//  FeedType.swift
//  HackerNews
//
//  Created by Trevor Elkins on 1/28/25.
//

public enum FeedType: CaseIterable {
  case top
  case new
  case best
  case ask
  case show

  public var title: String {
    switch self {
    case .top:
      return String(localized: "feed.type.top", bundle: .main)
    case .new:
      return String(localized: "feed.type.new", bundle: .main)
    case .best:
      return String(localized: "feed.type.best", bundle: .main)
    case .ask:
      return String(localized: "feed.type.ask", bundle: .main)
    case .show:
      return String(localized: "feed.type.show", bundle: .main)
    }
  }
}
