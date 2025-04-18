import SwiftUI
import WidgetKit

public enum ThemeContext {
  case app
  case widget
}

public enum FontStyle {
  case sans
  case mono
}

public enum FontStylePreference: String {
  case sans
  case sansAndMono

  public var displayName: String {
    switch self {
    case .sans:
      "Sans"
    case .sansAndMono:
      "Sans + Mono"
    }
  }
}

public enum FontFamilyPreference: String {
  case system
  case ibmPlex

  public var displayName: String {
    switch self {
    case .system:
      "System"
    case .ibmPlex:
      "IBM Plex"
    }
  }

  func font(size: CGFloat, style: FontStyle, weight: Font.Weight) -> Font {
    switch self {
    case .ibmPlex:
      switch style {
      case .sans:
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
      case .mono:
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
    case .system:
      let design: Font.Design = (style == .mono) ? .monospaced : .default
      return .system(size: size, weight: weight, design: design)
    }
  }
}

@MainActor
@Observable
public final class Theme {

  private static let useSystemFontKey = "useSystemFont"
  private static let useMonospacedKey = "useMonospaced"
  private static let commentFontSizeKey = "commentFontSize"
  private static let titleFontSizeKey = "titleFontSize"
  private static let fontFamilyKey = "fontFamily"
  private static let fontStyleKey = "fontStyle"

  /// Converts the legacy `useSystemFont` / `useMonospaced` keys into the new
  /// `fontFamilyPreference` / `fontStylePreference` keys the first time the
  /// app runs with this build.
  private static func migrateLegacyPreferences() {
    let defaults = UserDefaults.standard

    // ---- Font family ----
    // Only migrate if the legacy key actually exists *and* the new key is absent.
    if defaults.string(forKey: fontFamilyKey) == nil,
      let legacyUseSystem = defaults.object(forKey: useSystemFontKey) as? Bool
    {

      let family: FontFamilyPreference = legacyUseSystem ? .system : .ibmPlex
      defaults.set(family.rawValue, forKey: fontFamilyKey)
      // Remove the migrated legacy key so the check will not run again.
      defaults.removeObject(forKey: useSystemFontKey)
    }

    // ---- Font style ----
    // Only migrate if the legacy key actually exists *and* the new key is absent.
    if defaults.string(forKey: fontStyleKey) == nil,
      let legacyUseMono = defaults.object(forKey: useMonospacedKey) as? Bool
    {

      let style: FontStylePreference = legacyUseMono ? .sansAndMono : .sans
      defaults.set(style.rawValue, forKey: fontStyleKey)
      defaults.removeObject(forKey: useMonospacedKey)
    }
  }

  public static let defaultCommentFontSize: Double = 12
  public static let minCommentFontSize: Double = 10
  public static let maxCommentFontSize: Double = 18

  public static let defaultTitleFontSize: Double = 16
  public static let defaultWidgetTitleFontSize: Double = 13
  public static let minTitleFontSize: Double = 14
  public static let maxTitleFontSize: Double = 22

  private let context: ThemeContext

  public var fontFamilyPreference: FontFamilyPreference {
    didSet {
      UserDefaults.standard.set(
        fontFamilyPreference.rawValue,
        forKey: Self.fontFamilyKey
      )
    }
  }

  public var fontStylePreference: FontStylePreference {
    didSet {
      UserDefaults.standard
        .set(fontStylePreference.rawValue, forKey: Self.fontStyleKey)
    }
  }

  public var commentFontSize: Double {
    didSet {
      let clamped = commentFontSize.clamped(
        to: Self.minCommentFontSize...Self.maxCommentFontSize
      )
      if clamped != commentFontSize {
        commentFontSize = clamped
      }
      UserDefaults.standard.set(
        commentFontSize,
        forKey: Self.commentFontSizeKey
      )
    }
  }

  public var commentFontSizeText: String {
    String(format: "%.1f", commentFontSize)
  }

  public var titleFontSize: Double {
    didSet {
      let clamped = titleFontSize.clamped(
        to: Self.minTitleFontSize...Self.maxTitleFontSize
      )
      if clamped != titleFontSize {
        titleFontSize = clamped
      }
      UserDefaults.standard.set(titleFontSize, forKey: Self.titleFontSizeKey)
    }
  }

  public var titleFont: Font {
    let size = context == .app ? titleFontSize : Self.defaultWidgetTitleFontSize
    return themedFont(size: size, style: .mono, weight: .bold)
  }

  public var commentTextFont: Font {
    themedFont(size: commentFontSize, style: .mono, weight: .regular)
  }

  public var commentAuthorFont: Font {
    themedFont(size: commentFontSize, style: .mono, weight: .bold)
  }

  public var commentMetadataFont: Font {
    themedFont(size: commentFontSize, style: .sans, weight: .medium)
  }

  public func themedFont(
    size: CGFloat,
    style: FontStyle,
    weight: Font.Weight = .regular
  ) -> Font {
    var style = style
    if fontStylePreference == .sans {
      style = .sans
    }
    return fontFamilyPreference.font(size: size, style: style, weight: weight)
  }

  public init(context: ThemeContext = .app) {
    Self.migrateLegacyPreferences()
    self.context = context
    let fontStylePrefValue = UserDefaults.standard.string(
      forKey: Self.fontStyleKey
    )
    self.fontStylePreference =
      fontStylePrefValue
      .flatMap { FontStylePreference(rawValue: $0) } ?? .sansAndMono
    let fontFamilyPrefValue = UserDefaults.standard.string(
      forKey: Self.fontFamilyKey
    )
    self.fontFamilyPreference =
      fontFamilyPrefValue
      .flatMap { FontFamilyPreference(rawValue: $0) } ?? .ibmPlex
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
