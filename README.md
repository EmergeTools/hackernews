<p align="center">
  <img src="https://github.com/user-attachments/assets/61852a1b-4716-4893-8e54-4fd2a4399df1" alt="Cover image"/>
</p>

<a href="https://www.emergetools.com/app/example/android/com.emergetools.hackernews/release">
  <img src="https://img.shields.io/badge/dynamic/json?url=https%3A%2F%2Fwww.emergetools.com%2Fapi%2Fv2%2Fpublic_new_build%3FexampleId%3Dcom.emergetools.hackernews%26platform%3Dandroid%26badgeOption%3Dversion_and_max_download_size%26buildType%3Drelease&query=$.badgeMetadata&link=https%3A%2F%2Fwww.emergetools.com%2Fapp%2Fexample%2Fandroid%2Fcom.emergetools.hackernews%2Frelease&label=Hacker%20News&logo=android&color=#34A754" />
</a>

Welcome to the [Emerge Tools](https://www.emergetools.com/) Hacker News repo!

This repository serves as a practical Android & iOS example project, leveraging Emerge's suite of products for size analysis, snapshot testing, reaper (dead code detection), and performance testing.

The Android app is available on [Google Play](https://play.google.com/store/apps/details?id=com.emergetools.hackernews), and the iOS project is currently in development.

## Getting Started with Emerge

[Full docs](https://docs.emergetools.com/docs/quickstart)

### Example setup ([docs](https://docs.emergetools.com/docs/integrate-into-ci))

**Android:** The Android project in this repo use the [Emerge Gradle Plugin](https://docs.emergetools.com/docs/gradle-plugin) to upload to Emerge. Check out the [build.gradle.kts](https://github.com/EmergeTools/hackernews/blob/main/android/app/build.gradle.kts) for an example configuration.

**iOS**: The iOS project in this repo uses _[fastlane](https://docs.emergetools.com/docs/fastlane)_ to upload to Emerge. Check out the [Fastfile](https://github.com/EmergeTools/hackernews/blob/main/ios/fastlane/Fastfile#L137) for an example configuration.

## Questions

Feel free to open an issue or reach out to us directly if you have any questions or run into any issues.
