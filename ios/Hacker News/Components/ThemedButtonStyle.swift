//
//  ThemedButtonStyle.swift
//  Hacker News
//
//  Created by Trevor Elkins on 8/16/23.
//

import Foundation
import SwiftUI

struct ThemedButtonStyle: ButtonStyle {
  func makeBody(configuration: Self.Configuration) -> some View {
    configuration.label
      .foregroundColor(.white)
      .padding()
      .background(HNColors.orange)
      .cornerRadius(10)
  }
}

public struct ThemedButtonStyle2: ButtonStyle {
  public func makeBody(configuration: Self.Configuration) -> some View {
    configuration.label
      .foregroundColor(.white)
      .padding()
      .background(HNColors.orange)
      .cornerRadius(10)
  }
}

public struct ThemedButtonStyle3: ButtonStyle {
  public func makeBody(configuration: Self.Configuration) -> some View {
    configuration.label
      .foregroundColor(.white)
      .padding()
      .background(HNColors.orange)
      .cornerRadius(10)
  }
}

public struct ThemedButtonStyle4: ButtonStyle {
  public func makeBody(configuration: Self.Configuration) -> some View {
    configuration.label
      .foregroundColor(.white)
      .padding()
      .background(HNColors.orange)
      .cornerRadius(10)
  }
}

public struct ThemedButtonStyle5: ButtonStyle {
  public func makeBody(configuration: Self.Configuration) -> some View {
    configuration.label
      .foregroundColor(.white)
      .padding()
      .background(HNColors.orange)
      .cornerRadius(10)
  }
}

public struct ThemedButtonStyle6: ButtonStyle {
  public func makeBody(configuration: Self.Configuration) -> some View {
    configuration.label
      .foregroundColor(.white)
      .padding()
      .background(HNColors.orange)
      .cornerRadius(10)
  }
}

public struct ThemedButtonStyle7: ButtonStyle {
  public func makeBody(configuration: Self.Configuration) -> some View {
    configuration.label
      .foregroundColor(.white)
      .padding()
      .background(HNColors.orange)
      .cornerRadius(10)
  }
}

public struct ThemedButtonStyle8: ButtonStyle {
  public func makeBody(configuration: Self.Configuration) -> some View {
    configuration.label
      .foregroundColor(.white)
      .padding()
      .background(HNColors.orange)
      .cornerRadius(10)
  }
}

public struct ThemedButtonStyle9: ButtonStyle {
  public func makeBody(configuration: Self.Configuration) -> some View {
    configuration.label
      .foregroundColor(.white)
      .padding()
      .background(HNColors.orange)
      .cornerRadius(10)
  }
}

struct ThemedButtonStyle_PreviewProvider: PreviewProvider {
  static var previews: some View {
//    fatalError("Test error")
    Button("Test button3") {}
      .buttonStyle(ThemedButtonStyle())
      .previewLayout(.sizeThatFits)
  }
}
