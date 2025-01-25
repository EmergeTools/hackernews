import SwiftUI

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
  static let minTitleFontSize: Double = 14
  static let maxTitleFontSize: Double = 22

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
      let clamped = commentFontSize.clamped(to: Self.minCommentFontSize...Self.maxCommentFontSize)
      if clamped != commentFontSize {
        commentFontSize = clamped
      }
      UserDefaults.standard.set(commentFontSize, forKey: Self.commentFontSizeKey)
    }
  }

  var commentFontSizeText: String {
    String(format: "%.1f", commentFontSize)
  }

  var titleFontSize: Double {
    didSet {
      let clamped = titleFontSize.clamped(to: Self.minTitleFontSize...Self.maxTitleFontSize)
      if clamped != titleFontSize {
        titleFontSize = clamped
      }
      UserDefaults.standard.set(titleFontSize, forKey: Self.titleFontSizeKey)
    }
  }

  // Semantic font functions
  func commentTextFont() -> Font {
    if useSystemFont {
      return .system(
        size: commentFontSize, weight: .regular,
        design: useMonospaced ? .monospaced : .default)
    }
    return useMonospaced
      ? .ibmPlexMono(.regular, size: commentFontSize)
      : .ibmPlexSans(.regular, size: commentFontSize)
  }

  func commentAuthorFont() -> Font {
    if useSystemFont {
      return .system(
        size: commentFontSize, weight: .bold,
        design: useMonospaced ? .monospaced : .default)
    }
    return useMonospaced
      ? .ibmPlexMono(.bold, size: commentFontSize) : .ibmPlexSans(.bold, size: commentFontSize)
  }

  func commentMetadataFont() -> Font {
    if useSystemFont {
      return .system(
        size: commentFontSize, weight: .medium,
        design: useMonospaced ? .monospaced : .default)
    }
    return useMonospaced
      ? .ibmPlexMono(.medium, size: commentFontSize) : .ibmPlexSans(.medium, size: commentFontSize)
  }

  // Keep these for backward compatibility
  func userMonoFont(size: CGFloat, weight: Font.Weight = .regular) -> Font {
    if useSystemFont {
      return .system(size: size, weight: weight, design: .monospaced)
    }
    if !useMonospaced {
      return userSansFont(size: size, weight: weight)
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

  func titleFont() -> Font {
    if useSystemFont {
      return .system(
        size: titleFontSize, weight: .bold,
        design: useMonospaced ? .monospaced : .default)
    }
    return useMonospaced
      ? .ibmPlexMono(.bold, size: titleFontSize)
      : .ibmPlexSans(.bold, size: titleFontSize)
  }

  init() {
    self.useSystemFont = UserDefaults.standard.bool(forKey: Self.useSystemFontKey)
    self.useMonospaced =
      UserDefaults.standard.object(forKey: Self.useMonospacedKey) as? Bool ?? true
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
