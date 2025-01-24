//
//  WebView.swift
//  Hacker News
//
//  Created by Trevor Elkins on 8/17/23.
//

import Foundation
import SwiftUI
import UIKit
import WebKit

struct WebViewContainer: View {
  @Environment(\.openURL) var openURL

  let url: URL
  let title: String

  var body: some View {
    WebView(url: url)
      .navigationBarTitleDisplayMode(.inline)
      .navigationTitle(title)
      .toolbarBackground(.visible, for: .navigationBar)
      .toolbarBackground(Color(UIColor.systemBackground), for: .navigationBar)
      .toolbar {
        ToolbarItem(placement: .topBarTrailing) {
          Menu {
            Button {
              openURL(url)
            } label: {
              Label("Open in browser", systemImage: "safari")
            }
          } label: {
            Image(systemName: "ellipsis")
          }
        }
      }
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
