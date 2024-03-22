//
//  WebView.swift
//  Hacker News
//
//  Created by Trevor Elkins on 8/17/23.
//

import Foundation
import SwiftUI
import WebKit

struct WebView: UIViewRepresentable {
  let url: URL
  
  func makeUIView(context: Context) -> WKWebView {
    return WKWebView()
  }
  
  func updateUIView(_ webView: WKWebView, context: Context) {
    let request = URLRequest(url: url)
    webView.load(request)
  }
}
