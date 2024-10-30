//
//  ThemedButtonStyle.swift
//  Hacker News
//
//  Created by Trevor Elkins on 8/16/23.
//

import Foundation
import SwiftUI

struct InternalButtonStyle: ButtonStyle {
  func makeBody(configuration: Self.Configuration) -> some View {
    configuration.label
      .foregroundColor(.white)
      .padding()
      .background(HNColors.orange)
      .cornerRadius(10)
  }
}

#Preview {
  Button("Test") {
    
  }.buttonStyle(InternalButtonStyle())
}
