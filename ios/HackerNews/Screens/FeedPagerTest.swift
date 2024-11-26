//
//  FeedPagerTest.swift
//  HackerNews
//
//  Created by Rikin Marfatia on 11/25/24.
//

import Foundation
import SwiftUI

struct FeedPagerTest: View {
  @State private var selection = 0
  let tabs = ["Top", "New"]
  var body: some View {
    VStack(spacing: 0) {
      // Tab Bar
      HStack(spacing: 16.0) {
        ForEach(Array(tabs.enumerated()), id: \.offset) { index, title in
          Button(action: {
            withAnimation {
              selection = index
            }
          }) {
           Text(title)
              .foregroundColor(selection == index ? .blue : .gray)
              .scaleEffect(selection == index ? 1.2 : 1.0)
          }
        }
      }
      
      TabView(selection: $selection) {
        ForEach(Array(tabs.enumerated()), id: \.offset) { index, title in
          Text(title)
            .tag(index)
        }
      }
      .tabViewStyle(.page)
    }
  }
}

#Preview {
  FeedPagerTest()
}
