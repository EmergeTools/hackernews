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
        .copy("ibm_plex_sans_bold.ttf"),
        .copy("ibm_plex_sans_medium.ttf"),
        .copy("ibm_plex_sans_regular.ttf")
      ],
      swiftSettings: [
        .swiftLanguageMode(.v5)
      ]
    )
  ]
)
