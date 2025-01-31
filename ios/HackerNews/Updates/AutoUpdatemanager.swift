//
//  AutoUpdatemanager.swift
//  HackerNews
//
//  Created by Itay Brenner on 31/1/25.
//

#if ADHOC
import Foundation
import ETDistribution

struct AutoUpdateManager {
  @MainActor static func checkForUpdates() {
    let params = CheckForUpdateParams(apiKey: Constants.Distribution.apiKey)
    ETDistribution.shared.checkForUpdate(params: params) {
      switch $0 {
      case .success(let update):
        print("Update available: \(update)")
        guard let url = ETDistribution.shared.buildUrlForInstall(releaseInfo.downloadUrl) else {
          return
        }
        DispatchQueue.main.async {
          UIApplication.shared.open(url) { _ in
            exit(0)
          }
        }
      case .failure(let error):
        
        print("Error: \(error)")
      }
    }
  }
}
#endif
