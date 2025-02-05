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

final class WebViewModel: ObservableObject {
  @Published var url: URL
  @Published var isLoading: Bool = true
  
  init (url: URL) {
    self.url = url
  }
}

struct WebViewContainer: View {
  @Environment(\.openURL) var openURL
  
  @StateObject var model: WebViewModel

  let title: String
  
  init(url: URL, title: String) {
    self.title = title
    _model = StateObject(wrappedValue: WebViewModel(url: url))
  }

  var body: some View {
    LoadingView(isShowing: self.$model.isLoading) {
      WebView(viewModel: self.model)
    }
    .navigationBarTitleDisplayMode(.inline)
    .navigationTitle(title)
    .toolbarBackground(.visible, for: .navigationBar)
    .toolbarBackground(Color(UIColor.systemBackground), for: .navigationBar)
    .toolbar {
      ToolbarItem(placement: .topBarTrailing) {
        Menu {
          Button {
            openURL(self.model.url)
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
  @ObservedObject var viewModel: WebViewModel
  let webView = WKWebView(frame: .zero)

  func makeCoordinator() -> Coordinator {
    Coordinator(self.viewModel)
  }
  
  class Coordinator: NSObject, WKNavigationDelegate {
    private var viewModel: WebViewModel
    
    init(_ viewModel: WebViewModel) {
      self.viewModel = viewModel
    }

    func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
      self.viewModel.isLoading = false
    }
  }

  func makeUIView(context: Context) -> WKWebView {
    self.webView.navigationDelegate = context.coordinator
    self.webView.backgroundColor = .systemBackground
    self.webView.isOpaque = false

    return self.webView
  }

  func updateUIView(_ webView: WKWebView, context: Context) {
    self.webView.load(URLRequest(url: self.viewModel.url))
  }
}
