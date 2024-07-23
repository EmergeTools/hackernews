package com.emergetools.hackernews.features.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.emergetools.hackernews.data.HackerNewsWebClient
import com.emergetools.hackernews.data.LoginResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class LoginStatus {
  Idle,
  Success,
  Failed
}
data class LoginState(
  val username: String = "",
  val password: String = "",
  val status: LoginStatus = LoginStatus.Idle
)

sealed interface LoginAction {
  data class UsernameUpdated(val input: String): LoginAction
  data class PasswordUpdated(val input: String): LoginAction
  data object LoginSubmit: LoginAction
}

sealed interface LoginNavigation {
  data object Dismiss: LoginNavigation
}

class LoginViewModel(private val webClient: HackerNewsWebClient): ViewModel() {
  private val internalState = MutableStateFlow(LoginState())
  val state = internalState.asStateFlow()

  fun actions(action: LoginAction) {
    when(action) {
      LoginAction.LoginSubmit -> {
        viewModelScope.launch {
          val response = webClient.login(
            username = internalState.value.username,
            password = internalState.value.password
          )

          internalState.update { current ->
            current.copy(
              status = when (response) {
                LoginResponse.Success -> {
                  LoginStatus.Success
                }
                LoginResponse.Failed -> {
                  LoginStatus.Failed
                }
              }
            )
          }
        }
      }
      is LoginAction.PasswordUpdated -> {
        internalState.update { current ->
          current.copy(
            password = action.input
          )
        }
      }
      is LoginAction.UsernameUpdated -> {
        internalState.update { current ->
          current.copy(
            username = action.input
          )
        }
      }
    }
  }

  @Suppress("UNCHECKED_CAST")
  class Factory(private val webClient: HackerNewsWebClient): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      return LoginViewModel(webClient) as T
    }
  }
}