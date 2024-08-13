package com.emergetools.hackernews.features.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.emergetools.hackernews.ui.preview.AppStoreSnapshot
import com.emergetools.hackernews.ui.theme.HackerNewsTheme
import com.emergetools.hackernews.ui.theme.HackerRed
import com.emergetools.snapshots.annotations.EmergeAppStoreSnapshot

@Composable
fun LoginScreen(
  state: LoginState,
  actions: (LoginAction) -> Unit,
  navigation: (LoginNavigation) -> Unit
) {
  LaunchedEffect(state.status) {
    if (state.status == LoginStatus.Success) {
      navigation(LoginNavigation.Dismiss)
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(color = MaterialTheme.colorScheme.background),
    verticalArrangement = Arrangement.spacedBy(
      16.dp,
      alignment = Alignment.CenterVertically
    ),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      text = "Login",
      style = MaterialTheme.typography.titleMedium,
      color = MaterialTheme.colorScheme.onBackground,
    )
    TextField(
      value = state.username,
      shape = RoundedCornerShape(8.dp),
      colors = TextFieldDefaults.colors(
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
      ),
      keyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Next
      ),
      placeholder = { Text("Username") },
      trailingIcon = {
        if (state.status == LoginStatus.Failed) {
          Icon(
            imageVector = Icons.Rounded.Warning,
            tint = HackerRed,
            contentDescription = "Failed"
          )
        }
      },
      onValueChange = { actions(LoginAction.UsernameUpdated(it)) }
    )
    TextField(
      value = state.password,
      shape = RoundedCornerShape(8.dp),
      colors = TextFieldDefaults.colors(
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
      ),
      placeholder = { Text("Password") },
      keyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Password,
        imeAction = ImeAction.Done
      ),
      visualTransformation = PasswordVisualTransformation(),
      trailingIcon = {
        if (state.status == LoginStatus.Failed) {
          Icon(
            imageVector = Icons.Rounded.Warning,
            tint = HackerRed,
            contentDescription = "Failed"
          )
        }
      },
      onValueChange = { actions(LoginAction.PasswordUpdated(it)) }
    )
    Button(
      colors = ButtonDefaults.buttonColors(
        contentColor = MaterialTheme.colorScheme.onBackground
      ),
      onClick = { actions(LoginAction.LoginSubmit) }
    ) {
      Text(
        text = "Submit",
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold
      )
    }
  }
}

@OptIn(EmergeAppStoreSnapshot::class)
@PreviewLightDark
@AppStoreSnapshot
@Composable
private fun LoginScreenPreview() {
  HackerNewsTheme {
    LoginScreen(
      state = LoginState(),
      actions = {},
      navigation = {}
    )
  }
}
