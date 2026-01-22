//
//  SettingsScreen.swift
//  HackerNews
//
//  Created by Rikin Marfatia on 12/3/24.
//

import Foundation
import SwiftUI
import Common
import Kingfisher
import SentrySwiftUI

struct SettingsScreen: View {
  @Binding var model: AppViewModel
  @Environment(Theme.self) private var theme
  @State private var isShowingFeedback = false

  var body: some View {
    ScrollView {
      LazyVStack(spacing: 8) {
        VStack(alignment: .leading, spacing: 4) {
          Text("settings.section.profile")
            .font(theme.themedFont(size: 12, style: .sans, weight: .medium))
          LoginRow(loggedIn: model.authState == AuthState.loggedIn) {
            model.gotoLogin()
          }
        }

        VStack(alignment: .leading, spacing: 4) {
          Text("settings.section.about")
            .font(theme.themedFont(size: 12, style: .sans, weight: .medium))
          SettingsRow(
            text: String(localized: "settings.row.followEmerge"),
            leadingIcon: {
              Image(systemName: "bird.fill")
                .font(.system(size: 12))
                .foregroundStyle(.blue)
            },
            trailingIcon: {
              Image(systemName: "arrow.up.right")
                .font(.system(size: 12))
                .foregroundStyle(.onBackground)

            },
            action: {
              model.openSafariLink(
                url: URL(string: "https://www.x.com/emergetools")!)
            }
          )
          
          SettingsRow(
            text: String(localized: "settings.row.followSentry"),
            leadingIcon: {
              Image("Sentry")
                .font(.system(size: 12))
                .foregroundStyle(HNColors.sentry)
            },
            trailingIcon: {
              Image(systemName: "arrow.up.right")
                .font(.system(size: 12))
                .foregroundStyle(.onBackground)

            },
            action: {
              model.openSafariLink(
                url: URL(string: "https://www.x.com/sentry")!)
            }
          )

          SettingsRow(
            text: String(localized: "settings.row.followSupergooey"),
            leadingIcon: {
              Image(systemName: "bird.fill")
                .font(.system(size: 12))
                .foregroundStyle(.blue)
            },
            trailingIcon: {
              Image(systemName: "arrow.up.right")
                .font(.system(size: 12))
                .foregroundStyle(.onBackground)

            },
            action: {
              model.openSafariLink(
                url: URL(string: "https://www.x.com/heyrikin")!)
            }
          )

          SettingsRow(
            text: "Kingfisher demo",
            leadingIcon: {
              KFImage(URL(string: "https://placekitten.com/48/48"))
                .resizable()
                .frame(width: 20, height: 20)
                .clipShape(.rect(cornerRadius: 4))
            },
            trailingIcon: {
              Image(systemName: "arrow.up.right")
                .font(.system(size: 12))
                .foregroundStyle(.onBackground)
            },
            action: {
              model.openSafariLink(
                url: URL(string: "https://github.com/onevcat/Kingfisher")!)
            }
          )
          
#if ADHOC
          SettingsRow(
            text: String(localized: "settings.row.checkForUpdates"),
            leadingIcon: {
              Image(systemName: "icloud.and.arrow.down")
                .font(.system(size: 12))
                .foregroundStyle(.blue)
            },
            trailingIcon: {
              Image(systemName: "arrow.up.right")
                .font(.system(size: 12))
                .foregroundStyle(.onBackground)

            },
            action: {
              AutoUpdateManager.checkForUpdates()
            }
          )
#endif

          SettingsRow(
            text: String(localized: "settings.row.sendFeedback"),
            leadingIcon: {
              Image(systemName: "exclamationmark.triangle.fill")
                .font(.system(size: 12))
                .foregroundStyle(.yellow)
            },
            trailingIcon: {
              Image(systemName: "arrow.up.right")
                .font(.system(size: 12))
                .foregroundStyle(.onBackground)

            },
            action: {
              isShowingFeedback = true
            }
          )

          SettingsRow(
            text: String(localized: "settings.row.privacyPolicy"),
            leadingIcon: {
              Image(systemName: "lock.fill")
                .font(.system(size: 12))
                .foregroundStyle(.hnRed)
            },
            trailingIcon: {
              Image(systemName: "arrow.up.right")
                .font(.system(size: 12))
                .foregroundStyle(.onBackground)

            },
            action: {
              model.openLink(
                url: URL(
                  string:
                    "https://www.emergetools.com/HackerNewsPrivacyPolicy.html")!
              )
            }
          )
        }

        VStack(alignment: .leading, spacing: 4) {
          @Bindable var theme = theme
          Text("settings.section.appearance")
            .font(.ibmPlexSans(.medium, size: 12))
          
          SettingsRow(
            text: String(localized: "settings.row.fontFamily"),
            leadingIcon: {
              Image(systemName: "textformat")
                .font(.system(size: 12))
                .foregroundStyle(.purple)
            },
            trailingIcon: {
              Menu {
                Button(String(localized: "theme.fontFamily.system")) {
                  theme.fontFamilyPreference = .system
                }
                Button(String(localized: "theme.fontFamily.ibmPlex")) {
                  theme.fontFamilyPreference = .ibmPlex
                }
              } label: {
                HStack(spacing: 4) {
                  Text(
                    theme.fontFamilyPreference.displayName
                  )
                  .font(theme.themedFont(size: 14, style: .mono))
                  Image(systemName: "chevron.down")
                    .font(.system(size: 12))
                }
                .foregroundStyle(.onBackground)
              }
            },
            action: {}
          )
          
          SettingsRow(
            text: String(localized: "settings.row.fontStyle"),
            leadingIcon: {
              Image(systemName: "textformat")
                .font(.system(size: 12))
                .foregroundStyle(.purple)
            },
            trailingIcon: {
              Menu {
                Button(String(localized: "theme.fontStyle.sans")) {
                  theme.fontStylePreference = .sans
                }
                Button(String(localized: "theme.fontStyle.sansMono")) {
                  theme.fontStylePreference = .sansAndMono
                }
              } label: {
                HStack(spacing: 4) {
                  Text(
                    theme.fontStylePreference.displayName
                  )
                  .font(theme.themedFont(size: 14, style: .mono))
                  Image(systemName: "chevron.down")
                    .font(.system(size: 12))
                }
                .foregroundStyle(.onBackground)
              }
            },
            action: {}
          )

          SettingsRow(
            text: String(localized: "settings.commentFontSize", defaultValue: "Comment Font Size (\(String(format: "%.1f", theme.commentFontSize))pt)"),
            leadingIcon: {
              Image(systemName: "text.bubble")
                .font(.system(size: 12))
                .foregroundStyle(.blue)
            },
            trailingIcon: {
              Stepper(
                "",
                value: $theme.commentFontSize,
                in: Theme.minCommentFontSize...Theme.maxCommentFontSize,
                step: 0.5
              )
              .labelsHidden()
            },
            action: {}
          )
          .animation(.smooth, value: theme.commentFontSize)

          SettingsRow(
            text: String(localized: "settings.titleFontSize", defaultValue: "Title Font Size (\(String(format: "%.1f", theme.titleFontSize))pt)"),
            leadingIcon: {
              Image(systemName: "text.alignleft")
                .font(.system(size: 12))
                .foregroundStyle(.green)
            },
            trailingIcon: {
              Stepper(
                "",
                value: $theme.titleFontSize,
                in: Theme.minTitleFontSize...Theme.maxTitleFontSize,
                step: 0.5
              )
              .labelsHidden()
            },
            action: {}
          )
          .animation(.smooth, value: theme.titleFontSize)
        }
      }
      .padding(.horizontal, 8)
    }
    .safeAreaInset(edge: .top) {
      ZStack(alignment: .leading) {
        Color.clear
          .background(.ultraThinMaterial)
          .containerShape(.rect(cornerRadius: 24, style: .continuous))

        Text("settings.title")
          .font(.ibmPlexMono(.bold, size: 24))
          .padding(.horizontal, 16)
      }
      .frame(height: 60)
    }
    .sheet(isPresented: $isShowingFeedback) {
      SendFeedbackScreen()
    }
    .sentryTrace("SettingsScreen")
  }
}

#Preview {
  @Previewable @State var model = AppViewModel(
    bookmarkStore: FakeBookmarkDataStore(),
    shouldFetchPosts: false)
  SettingsScreen(model: $model)
    .environment(Theme())
}
