// swift-tools-version: 6.0
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
  name: "Fonts",
  platforms: [
    .iOS(.v17),
  ],
  products: [
    .library(
      name: "Fonts",
      type: .static,
      targets: ["Fonts"]
    )
  ],
  targets: [
    .target(
      name: "Fonts",
      resources: [
        .copy("IBMPlexSans-Bold.ttf"),
        .copy("IBMPlexSans-Medium.ttf"),
        .copy("IBMPlexSans-Regular.ttf"),
        .copy("IBMPlexMono-Bold.ttf"),
        .copy("IBMPlexMono-Medium.ttf"),
        .copy("IBMPlexMono-Regular.ttf")
      ],
      swiftSettings: [
        .swiftLanguageMode(.v5)
      ]
    )
  ]
)
