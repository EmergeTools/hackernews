//
//  NetworkDebugInterceptor.swift
//  HackerNews
//
//  Created by Trevor Elkins on 4/21/25.
//

import Foundation
import os

enum NetworkDebugInterceptor {
  static func register() {
    URLProtocol.registerClass(DebugProtocol.self)
  }
}

private final class DebugProtocol: URLProtocol {

  private var sessionTask: URLSessionDataTask?

  override class func canInit(with request: URLRequest) -> Bool {
    // Avoid infinite loop by skipping if we've already handled it
    return URLProtocol.property(forKey: #function, in: request) == nil
  }

  override class func canonicalRequest(for request: URLRequest) -> URLRequest {
    request
  }

  override func startLoading() {
    let taggedRequest =
      ((request as NSURLRequest).mutableCopy() as? NSMutableURLRequest)!
    URLProtocol.setProperty(true, forKey: #function, in: taggedRequest)

    let start = DispatchTime.now()

    sessionTask = URLSession.shared.dataTask(with: taggedRequest as URLRequest)
    { data, response, error in
      let elapsed =
        Double(DispatchTime.now().uptimeNanoseconds - start.uptimeNanoseconds)
        / 1_000_000

      self.log(
        request: taggedRequest as URLRequest,
        response: response,
        data: data,
        error: error,
        elapsed: elapsed
      )

      if let response = response {
        self.client?.urlProtocol(
          self,
          didReceive: response,
          cacheStoragePolicy: .notAllowed
        )
      }
      if let data = data { self.client?.urlProtocol(self, didLoad: data) }
      if let error = error {
        self.client?.urlProtocol(self, didFailWithError: error)
      } else {
        self.client?.urlProtocolDidFinishLoading(self)
      }
    }
    sessionTask?.resume()
  }

  override func stopLoading() {
    sessionTask?.cancel()
  }

  private func log(
    request: URLRequest,
    response: URLResponse?,
    data: Data?,
    error: Error?,
    elapsed: Double
  ) {
    let logger = os.Logger(
      subsystem: Bundle.main.bundleIdentifier ?? "NetworkDebug",
      category: "🌐"
    )

    var logLines: [String] = []
    logLines.append(
      "------ 🌐 \(request.httpMethod ?? "GET") \(request.url?.absoluteString ?? "")"
    )
    logLines.append("⏱ \(String(format: "%.2f ms", elapsed))")

    request.allHTTPHeaderFields?.forEach { logLines.append("➡️  \($0): \($1)") }

    if let body = request.httpBody, body.count > 0 {
      let pretty =
        body.prettyJSONString ?? String(decoding: body, as: UTF8.self)
      logLines.append("📤 Body:\n\(pretty.prefix(10_000))")  // cap large payloads
    }

    if let httpResp = response as? HTTPURLResponse {
      logLines.append("⬅️ Status: \(httpResp.statusCode)")

      httpResp.allHeaderFields.forEach { key, value in
        logLines.append("⬅️  \(key): \(value)")
      }

      if let d = data, d.count > 0 {
        let pretty = d.prettyJSONString ?? String(decoding: d, as: UTF8.self)
        logLines.append("📥 Body:\n\(pretty.prefix(10_000))")
      }
    }

    logLines.append("------ END 🌐\n")
    logger.debug("\n\(logLines.joined(separator: "\n"))")
  }
}

extension Data {
  fileprivate var prettyJSONString: String? {
    guard let obj = try? JSONSerialization.jsonObject(with: self) else {
      return nil
    }
    guard
      let pretty = try? JSONSerialization.data(
        withJSONObject: obj,
        options: .prettyPrinted
      )
    else { return nil }
    return String(decoding: pretty, as: UTF8.self)
  }
}
