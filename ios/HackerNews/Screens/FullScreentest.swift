//
//  Setup Screen.swift
//  HackerNews
//
//  Created by Rikin Marfatia on 11/27/24.
//

import Foundation
import SwiftUI

struct FullScreenTest: View {
  var body: some View {
    VStack {
      HStack {
        Text("Hello")
        Text("Hello")
      }
        ProgressView()
          .progressViewStyle(.circular)
          .frame(maxHeight: .infinity)
    }
  }
}

#Preview {
  FullScreenTest()
}
