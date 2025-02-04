//
//  AutoUpdatemanager.swift
//  HackerNews
//
//  Created by Itay Brenner on 31/1/25.
//

#if ADHOC
import UIKit
import Foundation
import ETDistribution

struct AutoUpdateManager {
  @MainActor static func checkForUpdates() {
    let params = CheckForUpdateParams(apiKey: Constants.Distribution.apiKey)
    ETDistribution.shared.checkForUpdate(params: params) { result in
      switch result {
      case .success(let releaseInfo):
        if let releaseInfo {
          print("Update found: \(releaseInfo)")
          guard let url = ETDistribution.shared.buildUrlForInstall(releaseInfo.downloadUrl) else {
            return
          }
          DispatchQueue.main.async {
            UIApplication.shared.open(url) { _ in
              exit(0)
            }
          }
        } else {
          print("Already up to date")
        }
      case .failure(let error):
        print("Error checking for update: \(error)")
      }
    }
  }
}
#endif
