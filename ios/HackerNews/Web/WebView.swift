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
import Common

@Observable final class WebViewModel {
  var url: URL
  var isLoading: Bool = true
  var progress: Double = 0.0
  
  init (url: URL) {
    self.url = url
  }
}

struct WebViewContainer: View {
  @Environment(\.openURL) var openURL
  
  @State var model: WebViewModel

  let title: String
  
  init(url: URL, title: String) {
    self.title = title
    self.model = WebViewModel(url: url)
  }

  var body: some View {
    ZStack {
      WebView(viewModel: self.$model)
      
      // Progress bar overlay positioned at the top
      VStack {
        if model.progress < 1.0 && model.isLoading {
          ProgressView(value: model.progress)
            .progressViewStyle(.linear)
            .frame(height: 3)
            .tint(HNColors.orange)
            .animation(.linear(duration: 0.1), value: model.progress)
        }
        Spacer()
      }
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
              Label(String(localized: "web.action.openInBrowser"), systemImage: "safari")
            }
          ShareLink(item: self.model.url) {
            Label(String(localized: "web.action.share"), systemImage: "square.and.arrow.up")
          }
        } label: {
          Image(systemName: "ellipsis")
        }
      }
    }
  }
}

struct WebView: UIViewRepresentable {
  @Binding var viewModel: WebViewModel
  let webView = WKWebView(frame: .zero)

  func makeCoordinator() -> Coordinator {
    Coordinator(self.viewModel)
  }
  
  class Coordinator: NSObject, WKNavigationDelegate {
    private var viewModel: WebViewModel
    private var progressObservation: NSKeyValueObservation?
    
    init(_ viewModel: WebViewModel) {
      self.viewModel = viewModel
    }
    
    func startObserving(_ webView: WKWebView) {
      progressObservation = webView.observe(\.estimatedProgress, options: .new) { [weak self] webView, _ in
        let progress = webView.estimatedProgress
        self?.viewModel.progress = progress
      }
    }
    
    func stopObserving() {
      progressObservation?.invalidate()
      progressObservation = nil
    }

    func webView(_ webView: WKWebView, didStartProvisionalNavigation navigation: WKNavigation!) {
      self.viewModel.isLoading = true
      self.viewModel.progress = 0.0
    }
    
    func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
      self.viewModel.isLoading = false
      self.viewModel.progress = 1.0
    }
    
    deinit {
      stopObserving()
    }
  }

  func makeUIView(context: Context) -> WKWebView {
    self.webView.navigationDelegate = context.coordinator
    self.webView.backgroundColor = .systemBackground
    self.webView.isOpaque = false
    context.coordinator.startObserving(self.webView)

    return self.webView
  }

  func updateUIView(_ webView: WKWebView, context: Context) {
    self.webView.load(URLRequest(url: self.viewModel.url))
  }
}
