//
//  WebView.swift
//  Hacker News
//
//  Created by Trevor Elkins on 8/17/23.
//

import Foundation
import SwiftUI
import WebKit

struct WebViewContainer: View {
  let url: URL
  let title: String

  var body: some View {
    WebView(url: url)
      .navigationBarTitleDisplayMode(.inline)
      .navigationTitle(title)
      .toolbarBackground(.visible, for: .navigationBar)
      .toolbarBackground(Color(UIColor.systemBackground), for: .navigationBar)
  }
}

struct WebView: UIViewRepresentable {
  let url: URL

  func makeUIView(context: Context) -> WKWebView {
    let configuration = WKWebViewConfiguration()
    let webView = WKWebView(frame: .zero, configuration: configuration)
    webView.backgroundColor = .systemBackground
    webView.isOpaque = false
    return webView
  }

  func updateUIView(_ webView: WKWebView, context: Context) {
    let request = URLRequest(url: url)
    webView.load(request)
  }
}
