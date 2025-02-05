//
//  ActivityIndicatorView.swift
//  HackerNews
//
//  Created by Itay Brenner on 5/2/25.
//

import SwiftUI

struct ActivityIndicatorView: UIViewRepresentable {
  @Binding var isAnimating: Bool
  let style: UIActivityIndicatorView.Style
    
  func makeUIView(context: UIViewRepresentableContext<ActivityIndicatorView>) -> UIActivityIndicatorView {
    return UIActivityIndicatorView(style: style)
  }
    
  func updateUIView(_ uiView: UIActivityIndicatorView, context: UIViewRepresentableContext<ActivityIndicatorView>) {
    isAnimating ? uiView.startAnimating() : uiView.stopAnimating()
  }
}
