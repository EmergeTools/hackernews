//
//  SendFeedbackScreen.swift
//  HackerNews
//
//  Created by Trevor Elkins on 4/16/25.
//

import SwiftUI
import Sentry

struct SendFeedbackScreen: View {
  // MARK: - State
  
  @State private var name: String = ""
  @State private var email: String = ""
  @State private var message: String = ""
  @State private var isSubmitted: Bool = false
  @Environment(\.dismiss) private var dismiss
  
  // MARK: - Body
  
  var body: some View {
    NavigationStack {
      Form {
        // Optional user details
        Section("Your Info (optional)") {
          TextField("Name", text: $name)
          TextField("Email", text: $email)
            .keyboardType(.emailAddress)
            .textContentType(.emailAddress)
            .autocapitalization(.none)
        }

        // Required feedback message
        Section("Your Feedback") {
          TextEditor(text: $message)
            .frame(minHeight: 150)
        }

        // Confirmation banner shown after submit
        if isSubmitted {
          Section {
            Label("Thank you for your feedback!", systemImage: "checkmark.seal.fill")
              .foregroundColor(.green)
          }
        }
      }
      .navigationTitle("Send Feedback")
      .navigationBarTitleDisplayMode(.inline)
      .toolbar {
        ToolbarItem(placement: .navigationBarTrailing) {
          Button(action: { dismiss() }) {
            Image(systemName: "xmark")
          }
        }
      }
    }
    // Submit button pinned to the bottom, inside the safe area
    .safeAreaInset(edge: .bottom) {
      Button(action: { sendFeedback() }) {
        Text("Submit")
          .font(.ibmPlexMono(.bold, size: 16))
          .frame(maxWidth: .infinity)
          .frame(height: 40)
      }
      .buttonStyle(.borderedProminent)
      .disabled(message.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty)
      .padding(.horizontal)
      .padding(.bottom)
    }
  }
  
  // MARK: - Sentry
  
  private func sendFeedback() {
    SentrySDK.capture(feedback: .init(
        message: message,
        name: name,
        email: email,
        source: .custom,
    ))
    
    // Reset form and show confirmation
    isSubmitted = true
    name = ""
    email = ""
    message = ""
  }
}
