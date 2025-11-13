package com.example.taskmanagerapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanagerapp.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val user: FirebaseUser? = null,
    val error: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow(AuthUiState())
    val authState: StateFlow<AuthUiState> = _authState

    init {
        observeAuthState()
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            authRepository.getAuthState().collect { user ->
                _authState.value = _authState.value.copy(user = user)
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null)
            val result = authRepository.login(email, password)
            _authState.value = result.fold(
                onSuccess = { _authState.value.copy(isLoading = false, user = it) },
                onFailure = { _authState.value.copy(isLoading = false, error = it.message) }
            )
        }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null)
            val result = authRepository.register(email, password)
            _authState.value = result.fold(
                onSuccess = { _authState.value.copy(isLoading = false, user = it) },
                onFailure = { _authState.value.copy(isLoading = false, error = it.message) }
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _authState.value = AuthUiState(user = null)
        }
    }
}
