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
      return "Top"
    case .new:
      return "New"
    case .best:
      return "Best"
    case .ask:
      return "Ask"
    case .show:
      return "Show"
    }
  }
}
