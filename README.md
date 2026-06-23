<p align="center">
  <img src="https://github.com/user-attachments/assets/61852a1b-4716-4893-8e54-4fd2a4399df1" alt="Cover image"/>
</p>

<a href="https://www.emergetools.com/app/example/ios/com.emergetools.hackernews/release?utm_campaign=badge-data">
  <img src="https://img.shields.io/badge/dynamic/json?url=https%3A%2F%2Fwww.emergetools.com%2Fapi%2Fv2%2Fpublic_new_build%3FexampleId%3Dcom.emergetools.hackernews%26platform%3Dios%26badgeOption%3Dversion_and_max_install_size%26buildType%3Drelease&query=$.badgeMetadata&label=HackerNews&logo=apple" />
</a>
<a href="https://www.emergetools.com/app/example/android/com.emergetools.hackernews/release">
  <img src="https://img.shields.io/badge/dynamic/json?url=https%3A%2F%2Fwww.emergetools.com%2Fapi%2Fv2%2Fpublic_new_build%3FexampleId%3Dcom.emergetools.hackernews%26platform%3Dandroid%26badgeOption%3Dversion_and_max_download_size%26buildType%3Drelease&query=$.badgeMetadata&link=https%3A%2F%2Fwww.emergetools.com%2Fapp%2Fexample%2Fandroid%2Fcom.emergetools.hackernews%2Frelease&label=Hacker%20News&logo=android&color=229D44" />
</a>

Welcome to the [Sentry](https://sentry.io/) Hacker News repo!

This repository serves as a practical Android & iOS example project, leveraging Sentry's suite of products for size analysis, snapshot testing and distribution.

The iOS app is available on the [App Store](https://apps.apple.com/us/app/hacker-news-by-emerge/id6740922950) and the Android app is available on [Google Play](https://play.google.com/store/apps/details?id=com.emergetools.hackernews).

## Getting Started

[Android docs](https://docs.sentry.io/platforms/android/)
[Apple docs](https://docs.sentry.io/platforms/apple/)

### Example Setup

This project uses the following GitHub actions workflows to upload builds and snapshot images to Sentry:

**Android:**
- [Snapshots](.github/workflows/android_sentry_upload_snapshots.yml)
- [Size analysis](.github/workflows/android_sentry_size_analysis.yml)
- [Distribution](.github/workflows/android_beta_build.yml)

The Android project in this repo uses the [Sentry Gradle Plugin](https://docs.sentry.io/platforms/android/configuration/gradle/) to upload to Sentry. Check out the [build.gradle.kts](android/app/build.gradle.kts) for an example configuration.

**iOS:**
- [Snapshots](.github/workflows/ios_sentry_upload_snapshots.yml)
- [Size analysis](.github/workflows/ios_sentry_upload_pr.yml)
- [Distribution](.github/workflows/ios_sentry_upload_adhoc.yml)

The iOS project in this repo uses _[fastlane](https://github.com/getsentry/sentry-fastlane-plugin)_ to upload to Sentry. Check out the [Fastfile](ios/fastlane/Fastfile) for an example configuration.

## Questions

Feel free to open an issue or reach out to us directly if you have any questions or run into any issues.
