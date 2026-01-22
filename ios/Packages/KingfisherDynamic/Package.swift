// swift-tools-version: 6.0
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
  name: "KingfisherDynamic",
  platforms: [
    .iOS(.v17),
  ],
  products: [
    .library(
      name: "KingfisherDynamic",
      type: .dynamic,
      targets: ["KingfisherDynamic"]
    )
  ],
  dependencies: [
    .package(url: "https://github.com/onevcat/Kingfisher.git", from: "8.6.2")
  ],
  targets: [
    .target(
      name: "KingfisherDynamic",
      dependencies: [
        .product(name: "Kingfisher", package: "Kingfisher")
      ],
      swiftSettings: [
        .swiftLanguageMode(.v5)
      ]
    )
  ]
)
