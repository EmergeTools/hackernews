//
//  SettingsScreen.swift
//  HackerNews
//
//  Created by Rikin Marfatia on 12/3/24.
//

import Foundation
import SwiftUI
import Common

struct SettingsScreen: View {
  @Binding var model: AppViewModel
  @Environment(Theme.self) private var theme
  @State private var isShowingFeedback = false

  var body: some View {
    ScrollView {
      LazyVStack(spacing: 8) {
        VStack(alignment: .leading, spacing: 4) {
          Text("Profile")
            .font(theme.userSansFont(size: 12, weight: .medium))
          LoginRow(loggedIn: model.authState == AuthState.loggedIn) {
            model.gotoLogin()
          }
        }

        VStack(alignment: .leading, spacing: 4) {
          Text("About")
            .font(theme.userSansFont(size: 12, weight: .medium))
          SettingsRow(
            text: "Follow Emerge",
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
              model.openLink(
                url: URL(string: "https://www.twitter.com/emergetools")!)

            }
          )

          SettingsRow(
            text: "Follow Supergooey",
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
              model.openLink(
                url: URL(string: "https://www.twitter.com/heyrikin")!)
            }
          )
          
#if ADHOC
          SettingsRow(
            text: "Check for Updates",
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
            text: "Send Feedback",
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
            text: "Privacy Policy",
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
          Text("Appearance")
            .font(.ibmPlexSans(.medium, size: 12))
          
          SettingsRow(
            text: "Font Family",
            leadingIcon: {
              Image(systemName: "textformat")
                .font(.system(size: 12))
                .foregroundStyle(.purple)
            },
            trailingIcon: {
              Menu {
                Button("System") {
                  theme.useSystemFont = true
                }
                Button("IBM Plex") {
                  theme.useSystemFont = false
                }
              } label: {
                HStack(spacing: 4) {
                  Text(
                    theme.useSystemFont
                      ? "System"
                      : "IBM Plex"
                  )
                  .font(theme.userMonoFont(size: 14))
                  Image(systemName: "chevron.down")
                    .font(.system(size: 12))
                }
                .foregroundStyle(.onBackground)
              }
            },
            action: {}
          )
          
          SettingsRow(
            text: "Font Style",
            leadingIcon: {
              Image(systemName: "textformat")
                .font(.system(size: 12))
                .foregroundStyle(.purple)
            },
            trailingIcon: {
              Menu {
                Button("Regular") {
                  theme.useMonospaced = false
                }
                Button("Regular + Mono") {
                  theme.useMonospaced = true
                }
              } label: {
                HStack(spacing: 4) {
                  Text(
                    theme.useMonospaced
                      ? "Regular + Mono"
                      : "Regular"
                  )
                  .font(theme.userMonoFont(size: 14))
                  Image(systemName: "chevron.down")
                    .font(.system(size: 12))
                }
                .foregroundStyle(.onBackground)
              }
            },
            action: {}
          )

          SettingsRow(
            text:
              "Comment Font Size (\(String(format: "%.1f", theme.commentFontSize))pt)",
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
            text:
              "Title Font Size (\(String(format: "%.1f", theme.titleFontSize))pt)",
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

        Text("Settings")
          .font(.ibmPlexMono(.bold, size: 24))
          .padding(.horizontal, 16)
      }
      .frame(height: 60)
    }
    .sheet(isPresented: $isShowingFeedback) {
      SendFeedbackScreen()
    }
  }
}

#Preview {
  @Previewable @State var model = AppViewModel(
    bookmarkStore: FakeBookmarkDataStore(),
    shouldFetchPosts: false)
  SettingsScreen(model: $model)
    .environment(Theme())
}
