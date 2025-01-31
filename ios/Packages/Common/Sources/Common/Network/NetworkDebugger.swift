//
//  NetworkDebugger.swift
//  Hacker News
//
//  Created by Trevor Elkins on 3/21/24.
//

import Foundation

// Prints out interesting stats for a URLResponse!
class NetworkDebugger {
  static func printStats(for response: URLResponse) {
    guard let httpResponse = response as? HTTPURLResponse else {
      print("The response is not an HTTP URL response.")
      return
    }
    
    if let url = httpResponse.url {
      print("URL: \(url.absoluteString)")
    }
    
    print("Status Code: \(httpResponse.statusCode)")
    
    if let mimeType = httpResponse.mimeType {
      print("MIME Type: \(mimeType)")
    }
    
    print("Expected Content Length: \(httpResponse.expectedContentLength)")
    
    print("Header Fields:")
    for (key, value) in httpResponse.allHeaderFields {
      print("\(key): \(value)")
    }
  }
}
