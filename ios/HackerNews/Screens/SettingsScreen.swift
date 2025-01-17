//
//  SettingsScreen.swift
//  HackerNews
//
//  Created by Rikin Marfatia on 12/3/24.
//

import Foundation
import SwiftUI

struct SettingsScreen: View {
  @ObservedObject var model: AppViewModel

  var body: some View {
    ScrollView {
      LazyVStack {

      }
    }
    .overlay {
      ZStack(alignment: .leading) {
        Color.clear
          .background(.ultraThinMaterial)
          .containerShape(.rect(cornerRadius: 24, style: .continuous))

        Text("Settings")
          .font(.custom("IBMPlexMono-Bold", size: 24))
          .padding(.horizontal, 16)
      }
      .frame(height: 60)
      .frame(maxHeight: .infinity, alignment: .top)
    }
  }
}


#Preview {
  SettingsScreen(model: AppViewModel(bookmarkStore: FakeBookmarkDataStore()))
}
