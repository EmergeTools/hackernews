//
//  StoryRowV2.swift
//  HackerNews
//
//  Created by Rikin Marfatia on 11/20/24.
//

import SwiftUI

struct StoryRowV2: View {
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text("@heyrikin")
                .foregroundColor(.hnOrange)
                .fontWeight(/*@START_MENU_TOKEN@*/.bold/*@END_MENU_TOKEN@*/)
            Text("A cool new HN client for iOS")
                .font(.headline)
            HStack(spacing: 16) {
                HStack {
                    Image(systemName: "arrow.up")
                        .foregroundColor(.green)
                    Text("99")
                }
                HStack {
                    Image(systemName: "clock")
                        .foregroundColor(.purple)
                    Text("2h ago")
                }
                Spacer()
                HStack {
                    Image(systemName: "bubble.right.fill")
                        .foregroundColor(.blue)
                    Text("100")
                }
                .padding(8)
                .background(.commentBackground)
                .cornerRadius(.infinity)
            }
        }
        .padding()
    }
}

#Preview {
    StoryRowV2()
}
