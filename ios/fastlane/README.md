fastlane documentation
----

# Installation

Make sure you have the latest version of the Xcode command line tools installed:

```sh
xcode-select --install
```

For _fastlane_ installation instructions, see [Installing _fastlane_](https://docs.fastlane.tools/#installing-fastlane)

# Available Actions

## iOS

### ios load_asc_api_key

```sh
[bundle exec] fastlane ios load_asc_api_key
```

Load ASC API Key information to use in subsequent lanes

### ios fetch_and_increment_build_number

```sh
[bundle exec] fastlane ios fetch_and_increment_build_number
```

Bump build number based on most recent TestFlight build number

### ios prepare_signing

```sh
[bundle exec] fastlane ios prepare_signing
```

Installs signing certificate in the keychain and downloads provisioning profiles from App Store Connect

### ios build_app_for_scheme

```sh
[bundle exec] fastlane ios build_app_for_scheme
```

Build the iOS app

### ios upload_app

```sh
[bundle exec] fastlane ios upload_app
```

Upload to TestFlight / ASC

### ios build_upload_testflight

```sh
[bundle exec] fastlane ios build_upload_testflight
```

Build and upload to TestFlight + Emerge Tools

### ios build_upload_emerge

```sh
[bundle exec] fastlane ios build_upload_emerge
```

Build and upload to Emerge Tools

### ios build_upload_emerge_snapshot

```sh
[bundle exec] fastlane ios build_upload_emerge_snapshot
```

Build and upload snapshot build to Emerge Tools

----

This README.md is auto-generated and will be re-generated every time [_fastlane_](https://fastlane.tools) is run.

More information about _fastlane_ can be found on [fastlane.tools](https://fastlane.tools).

The documentation of _fastlane_ can be found on [docs.fastlane.tools](https://docs.fastlane.tools).
