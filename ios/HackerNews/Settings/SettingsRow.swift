//
//  SettingsRow.swift
//  HackerNews
//
//  Created by Rikin Marfatia on 1/17/25.
//

import Foundation
import SwiftUI

struct SettingsRow<Leading: View, Trailing: View>: View {
  let text: String
  @ViewBuilder let leadingIcon: () -> Leading
  @ViewBuilder let trailingIcon: () -> Trailing
  let action: () -> Void

  var body: some View {
    HStack(alignment: .center, spacing: 8) {
      leadingIcon()
      Text(text)
        .font(.ibmPlexMono(.bold, size: 16))
      Spacer()
      trailingIcon()
    }
    .frame(maxWidth: .infinity, alignment: .leading)
    .padding(16)
    .background(.surface)
    .clipShape(.rect(cornerRadius: 16))
    .onTapGesture {
      action()
    }
  }
}

#Preview {
  SettingsRow(
    text: "Follow Emerge",
    leadingIcon: {
      Image(systemName: "person.crop.circle")
        .font(.system(size: 12))
    },
    trailingIcon: {
      Image(systemName: "arrow.up.right")
        .font(.system(size: 12))
    },
    action: {}
  )
}
