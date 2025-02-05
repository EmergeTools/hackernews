//
//  LoadingView.swift
//  HackerNews
//
//  Created by Itay Brenner on 5/2/25.
//

import SwiftUI
import Common

struct LoadingView<Content>: View where Content: View {
  @Binding var isShowing: Bool
  var content: () -> Content
  
  var body: some View {
    GeometryReader { geometry in
      ZStack(alignment: .center) {
        self.content()
          .disabled(self.isShowing)
          .blur(radius: self.isShowing ? 3 : 0)
        
        VStack {
          Text("Loading...")
          ProgressView()
            .controlSize(.large)
        }
        .frame(width: geometry.size.width / 2, height: geometry.size.height / 5)
        .background(Color.secondary.colorInvert())
        .foregroundColor(HNColors.orange)
        .cornerRadius(20)
        .opacity(self.isShowing ? 1 : 0)
        
      }
    }
  }
}

#Preview {
  LoadingView(isShowing: .constant(true)) {
    Rectangle().fill(Color.black)
      .frame(width: 400, height: 400)
  }
}
