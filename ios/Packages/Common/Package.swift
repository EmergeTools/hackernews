// swift-tools-version: 6.0
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
  name: "Common",
  defaultLocalization: "en",
  platforms: [
    .iOS(.v17),
  ],
  products: [
    .library(
      name: "Common",
      type: .dynamic,
      targets: ["Common"]
    )
  ],
  targets: [
    .target(
      name: "Common",
      swiftSettings: [
        .swiftLanguageMode(.v5)
      ]
    )
  ]
)
