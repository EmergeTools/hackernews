import Foundation

public enum AppRuntime {
  public static var isRunningForSnapshots: Bool {
    let environment = ProcessInfo.processInfo.environment
    return environment["SENTRY_IS_RUNNING_FOR_SNAPSHOTS"] == "1"
      || environment["XCODE_RUNNING_FOR_PREVIEWS"] == "1"
  }
}
