//
//  LoginScreen.swift
//  HackerNews
//
//  Created by Rikin Marfatia on 12/30/24.
//

import Foundation
import SwiftUI

struct LoginScreen: View {
  @State var username: String = ""
  @State var password: String = ""

  var body: some View {
    VStack(alignment: .center) {
      Text("Login")
      TextField(text: $username) {
        Text("Username")
      }
      TextField(text: $password) {
        Text("Password")
      }
    }
  }
}

#Preview {
  LoginScreen()
}
