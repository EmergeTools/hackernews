import SwiftUI

@MainActor
@Observable
final class Theme {
  private static let useSystemFontKey = "useSystemFont"
  private static let useMonospacedKey = "useMonospaced"

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

  func userMonoFont(size: CGFloat, weight: Font.Weight = .regular) -> Font {
    if useSystemFont {
      return Font.system(size: size, weight: weight, design: .monospaced)
    }
    if !useMonospaced {
      return userSansFont(size: size, weight: weight)
    }
    switch weight {
    case .regular:
      return Font.ibmPlexMono(.regular, size: size)
    case .bold:
      return Font.ibmPlexMono(.bold, size: size)
    case .medium:
      return Font.ibmPlexMono(.medium, size: size)
    default:
      return Font.ibmPlexMono(.regular, size: size)
    }
  }

  func userSansFont(size: CGFloat, weight: Font.Weight = .regular) -> Font {
    if useSystemFont {
      return Font.system(size: size, weight: weight, design: .monospaced)
    }
    switch weight {
    case .regular:
      return Font.ibmPlexSans(.regular, size: size)
    case .bold:
      return Font.ibmPlexSans(.bold, size: size)
    case .medium:
      return Font.ibmPlexSans(.medium, size: size)
    default:
      return Font.ibmPlexSans(.regular, size: size)
    }
  }

  init() {
    self.useSystemFont = UserDefaults.standard.bool(forKey: Self.useSystemFontKey)
    self.useMonospaced =
      UserDefaults.standard.object(forKey: Self.useMonospacedKey) as? Bool ?? true
  }
}
