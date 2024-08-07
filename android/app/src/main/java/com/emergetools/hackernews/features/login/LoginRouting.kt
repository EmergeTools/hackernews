package com.emergetools.hackernews.features.login

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.dialog
import com.emergetools.hackernews.webClient
import kotlinx.serialization.Serializable

sealed interface LoginDestinations {
  @Serializable
  data object Login : LoginDestinations
}

fun NavGraphBuilder.loginRoutes(navController: NavController) {
  dialog<LoginDestinations.Login>(
    dialogProperties = DialogProperties(
      usePlatformDefaultWidth = false,
      decorFitsSystemWindows = false
    )
  ) {
    val context = LocalContext.current
    val model = viewModel<LoginViewModel>(
      factory = LoginViewModel.Factory(
        webClient = context.webClient()
      )
    )
    val state by model.state.collectAsState()
    LoginScreen(
      state = state,
      actions = model::actions,
      navigation = { place ->
        when (place) {
          is LoginNavigation.Dismiss -> {
            navController.popBackStack()
          }
        }
      }
    )
  }
}
