#if ADHOC
import SentryDistribution

struct AutoUpdateManager {
  @MainActor static func checkForUpdates() {
    let params = CheckForUpdateParams(
      accessToken: Constants.Distribution.accessToken,
      organization: "sentry",
      project: "hackernews-ios"
    )

    Updater.checkForUpdate(params: params) { result in
      switch result {
      case .success(let response):
        guard let update = response.update else {
          print("Already up to date")
          return
        }

        print("Update found: \(update)")
        guard let url = Updater.buildUrlForInstall(update.downloadUrl) else {
          return
        }

        Task { @MainActor in
          Updater.install(url: url)
        }
      case .failure(let error):
        print("Error checking for update: \(error)")
      }
    }
  }
}
#endif
