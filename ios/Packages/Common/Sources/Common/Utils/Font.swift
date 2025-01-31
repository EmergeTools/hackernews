import SwiftUI

public extension Font {
  enum IBMPlexSans {
    case regular
    case medium
    case bold

    public var name: String {
      switch self {
      case .regular: "IBMPlexSans-Regular"
      case .medium: "IBMPlexSans-Medium"
      case .bold: "IBMPlexSans-Bold"
      }
    }
  }

  enum IBMPlexMono {
    case regular
    case medium
    case bold

    public var name: String {
      switch self {
      case .regular: "IBMPlexMono-Regular"
      case .medium: "IBMPlexMono-Medium"
      case .bold: "IBMPlexMono-Bold"
      }
    }
  }

  static func ibmPlexSans(_ type: IBMPlexSans, size: CGFloat) -> Font {
    .custom(type.name, size: size)
  }

  static func ibmPlexMono(_ type: IBMPlexMono, size: CGFloat) -> Font {
    .custom(type.name, size: size)
  }
}
