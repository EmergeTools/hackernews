import SwiftUI
import WidgetKit

enum ThemeContext {
  case app
  case widget
}

@MainActor
@Observable
final class Theme {
  private static let useSystemFontKey = "useSystemFont"
  private static let useMonospacedKey = "useMonospaced"
  private static let commentFontSizeKey = "commentFontSize"
  private static let titleFontSizeKey = "titleFontSize"

  static let defaultCommentFontSize: Double = 12
  static let minCommentFontSize: Double = 10
  static let maxCommentFontSize: Double = 18

  static let defaultTitleFontSize: Double = 16
  static let defaultWidgetTitleFontSize: Double = 13
  static let minTitleFontSize: Double = 14
  static let maxTitleFontSize: Double = 22

  private let context: ThemeContext

  var useSystemFont: Bool {
    didSet {
      UserDefaults.standard.set(useSystemFont, forKey: Self.useSystemFontKey)
    }
  }

  var useMonospaced: Bool {
    didSet {
      UserDefaults.standard.set(useMonospaced, forKey: Self.useMonospacedKey)
    }
  }

  var commentFontSize: Double {
    didSet {
      let clamped = commentFontSize.clamped(
        to: Self.minCommentFontSize...Self.maxCommentFontSize)
      if clamped != commentFontSize {
        commentFontSize = clamped
      }
      UserDefaults.standard.set(
        commentFontSize, forKey: Self.commentFontSizeKey)
    }
  }

  var commentFontSizeText: String {
    String(format: "%.1f", commentFontSize)
  }

  var titleFontSize: Double {
    didSet {
      let clamped = titleFontSize.clamped(
        to: Self.minTitleFontSize...Self.maxTitleFontSize)
      if clamped != titleFontSize {
        titleFontSize = clamped
      }
      UserDefaults.standard.set(titleFontSize, forKey: Self.titleFontSizeKey)
    }
  }

  var titleFont: Font {
    let size = context == .app ? titleFontSize : Self.defaultWidgetTitleFontSize
    return userMonoFont(size: size, weight: .bold)
  }

  var commentTextFont: Font {
    userMonoFont(size: commentFontSize, weight: .regular)
  }

  var commentAuthorFont: Font {
    userMonoFont(size: commentFontSize, weight: .bold)
  }

  var commentMetadataFont: Font {
    userSansFont(size: commentFontSize, weight: .medium)
  }

  func userMonoFont(size: CGFloat, weight: Font.Weight = .regular) -> Font {
    if !useMonospaced {
      return userSansFont(size: size, weight: weight)
    }
    if useSystemFont {
      return .system(size: size, weight: weight, design: .default)
    }
    switch weight {
    case .regular:
      return .ibmPlexMono(.regular, size: size)
    case .bold:
      return .ibmPlexMono(.bold, size: size)
    case .medium:
      return .ibmPlexMono(.medium, size: size)
    default:
      return .ibmPlexMono(.regular, size: size)
    }
  }

  func userSansFont(size: CGFloat, weight: Font.Weight = .regular) -> Font {
    if useSystemFont {
      return .system(size: size, weight: weight, design: .default)
    }
    switch weight {
    case .regular:
      return .ibmPlexSans(.regular, size: size)
    case .bold:
      return .ibmPlexSans(.bold, size: size)
    case .medium:
      return .ibmPlexSans(.medium, size: size)
    default:
      return .ibmPlexSans(.regular, size: size)
    }
  }

  init(context: ThemeContext = .app) {
    self.context = context
    self.useSystemFont =
      UserDefaults.standard.object(forKey: Self.useSystemFontKey) as? Bool
      ?? false
    self.useMonospaced =
      UserDefaults.standard.object(forKey: Self.useMonospacedKey) as? Bool
      ?? true
    self.commentFontSize =
      UserDefaults.standard.object(forKey: Self.commentFontSizeKey) as? Double
      ?? Self.defaultCommentFontSize
    self.titleFontSize =
      UserDefaults.standard.object(forKey: Self.titleFontSizeKey) as? Double
      ?? Self.defaultTitleFontSize
  }
}

extension Double {
  fileprivate func clamped(to range: ClosedRange<Double>) -> Double {
    min(max(self, range.lowerBound), range.upperBound)
  }
}
