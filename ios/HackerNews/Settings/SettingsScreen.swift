//
//  SettingsScreen.swift
//  HackerNews
//
//  Created by Rikin Marfatia on 12/3/24.
//

import Foundation
import SwiftUI

struct SettingsScreen: View {
  @ObservedObject var model: AppViewModel

  var body: some View {
    ScrollView {
      LazyVStack(spacing: 8) {
        Spacer()
          .frame(height: 68)
        VStack(alignment: .leading, spacing: 4) {
          Text("Profile")
            .font(.ibmPlexSans(.medium, size: 12))
          LoginRow(loggedIn: model.authState == AuthState.loggedIn) {
            model.gotoLogin()
          }
        }

        VStack(alignment: .leading, spacing: 4) {
          Text("About")
            .font(.ibmPlexSans(.medium, size: 12))
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
              model.openLink(url: URL(string: "https://www.twitter.com/emergetools")!)

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
              model.openLink(url: URL(string: "https://www.twitter.com/heyrikin")!)
            }
          )

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
              model.openLink(url: URL(string: "https://forms.gle/YYno9sUehE5xuKAq9")!)
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
                url: URL(string: "https://www.emergetools.com/HackerNewsPrivacyPolicy.html")!)
            }
          )
        }
      }
      .padding(.horizontal, 8)
    }
    .overlay {
      ZStack(alignment: .leading) {
        Color.clear
          .background(.ultraThinMaterial)
          .containerShape(.rect(cornerRadius: 24, style: .continuous))

        Text("Settings")
          .font(.ibmPlexMono(.bold, size: 24))
          .padding(.horizontal, 16)
      }
      .frame(height: 60)
      .frame(maxHeight: .infinity, alignment: .top)
    }
  }
}

#Preview {
  SettingsScreen(model: AppViewModel(bookmarkStore: FakeBookmarkDataStore()))
}
