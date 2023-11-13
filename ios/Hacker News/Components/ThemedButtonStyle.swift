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

struct ThemedButtonStyle_PreviewProvider: PreviewProvider {
  static var previews: some View {
    var test = ["one", "two"]
    var string = test[3]
    Button(string) {}
      .buttonStyle(ThemedButtonStyle())
      .previewLayout(.sizeThatFits)
  }
}
