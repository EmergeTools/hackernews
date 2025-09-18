import Sentry

struct Logger {
    static func debug(_ message: String) {
        print("[DEBUG] \(message)")
        SentrySDK.logger.debug(message)
    }

    static func info(_ message: String) {
        print(message)
        SentrySDK.logger.info(message)
    }

    static func error(_ message: String) {
        print(message)
        SentrySDK.logger.error(message)
    }
}
