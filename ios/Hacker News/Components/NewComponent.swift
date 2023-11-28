//
//  NewComponent.swift
//  Hacker News
//
//  Created by Itay Brenner on 28/11/23.
//

import SwiftUI

struct NewComponent: View {
  var body: some View {
    VStack {
      Text("Example View")
        .foregroundColor(.black)
      Button("Ok") {}
        .foregroundColor(.cyan)
        .padding()
    }
    .padding()
    .cornerRadius(20)
    .overlay(
      RoundedRectangle(cornerRadius: 20)
        .stroke(.blue, lineWidth: 5)
    )
  }
}

struct NewComponent_Preview: PreviewProvider {
  static var previews: some View {
    Group {
      ForEach(ColorScheme.allCases, id: \.self) {
        NewComponent().preferredColorScheme($0)
      }
    }
  }
}
