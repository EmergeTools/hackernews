//
//  SendFeedbackScreen.swift
//  HackerNews
//
//  Created by Trevor Elkins on 4/16/25.
//

import Sentry
import SwiftUI

struct SendFeedbackScreen: View {

  @State private var name: String = ""
  @State private var email: String = ""
  @State private var message: String = ""
  @State private var isSubmitted: Bool = false
  @Environment(\.dismiss) private var dismiss

  var body: some View {
    NavigationStack {
      Form {
        Section("Your Info (optional)") {
          TextField("Name", text: $name)
          TextField("Email", text: $email)
            .keyboardType(.emailAddress)
            .textContentType(.emailAddress)
            .autocapitalization(.none)
        }

        Section("Your Feedback") {
          TextEditor(text: $message)
            .frame(minHeight: 150)
        }

        if isSubmitted {
          Section {
            Label(
              "Thank you for your feedback!",
              systemImage: "checkmark.seal.fill"
            )
            .foregroundColor(.green)
            .frame(maxWidth: .infinity, alignment: .center)
            .transition(.scale.combined(with: .opacity))
          }
        }
      }
      .navigationTitle("Send Feedback")
      .navigationBarTitleDisplayMode(.inline)
      .animation(.spring(), value: isSubmitted)
      .toolbar {
        ToolbarItem(placement: .navigationBarTrailing) {
          Button(action: { dismiss() }) {
            Image(systemName: "xmark")
              .font(.system(size: 10, weight: .semibold))
              .foregroundColor(.secondary)
              .padding(8)
              .background(Color(.systemGray5))
              .clipShape(Circle())
          }
        }
      }
    }
    .safeAreaInset(edge: .bottom) {
      Button(action: { sendFeedback() }) {
        Text("Submit")
          .font(.ibmPlexMono(.bold, size: 16))
          .frame(maxWidth: .infinity)
          .frame(height: 40)
      }
      .buttonStyle(.borderedProminent)
      .disabled(
        message.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty
          || isSubmitted
      )
      .padding(.horizontal)
      .padding(.bottom)
    }
  }

  private func sendFeedback() {
    let feedback = SentryFeedback(
      message: message,
      name: name,
      email: email,
      source: .custom,
    )
    SentrySDK.capture(feedback: feedback)

    withAnimation {
      isSubmitted = true
    }

    // Auto dismiss after a brief delay
    DispatchQueue.main.asyncAfter(deadline: .now() + 2.0) {
      dismiss()
    }
  }
}

#Preview {
  SendFeedbackScreen()
}
