//
//  CommentsHeader.swift
//  HackerNews
//
//  Created by Rikin Marfatia on 12/23/24.
//

import SwiftUI


struct CommentsHeader: View {
  var body: some View {
    VStack(alignment: .leading) {
      Text("The journey to save the last known 43-inch Sony CRT")
        .font(.title2)
        .fontWeight(.bold)
        .frame(maxWidth: .infinity, alignment: .leading)
      HStack {
        // post author
        Text("@author")
          .fontWeight(.bold)
          .foregroundColor(Color.hnOrange)
        // post time
        HStack(alignment: .center, spacing: 4.0) {
          Image(systemName: "clock")
          Text("4h ago")
        }
        Spacer()
        // upvote button
        Button(action: {}) {
          Image(systemName: "arrow.up")
            .font(.caption2)
        }
        .padding(
          EdgeInsets(
            top: 4.0,
            leading: 8.0,
            bottom: 4.0,
            trailing: 8.0
          )
        )
        .background(HNColors.background)
        .foregroundStyle(.black)
        .clipShape(Capsule())
      }
    }
  }
}

#Preview {
  CommentsHeader()
}
