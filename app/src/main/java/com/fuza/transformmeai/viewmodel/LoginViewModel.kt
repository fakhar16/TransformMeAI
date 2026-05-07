package com.fuza.transformmeai.viewmodel

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.fuza.transformmeai.data.session.TransformSessionStore
import com.fuza.transformmeai.domain.model.LoadableUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val googleSignInClient: GoogleSignInClient,
    private val sessionStore: TransformSessionStore,
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoadableUiState<Unit>>(LoadableUiState.Idle)
    val uiState: StateFlow<LoadableUiState<Unit>> = _uiState.asStateFlow()

    fun signInIntent(): Intent = googleSignInClient.signInIntent

    fun handleSignInResult(data: Intent?) {
        viewModelScope.launch {
            if (data == null) {
                _uiState.value = LoadableUiState.Idle
                return@launch
            }
            _uiState.value = LoadableUiState.Loading
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    sessionStore.setLoggedIn(true)
                    _uiState.value = LoadableUiState.Success(Unit)
                } else {
                    _uiState.value = LoadableUiState.Error("Unable to read Google account.")
                }
            } catch (e: ApiException) {
                _uiState.value =
                    LoadableUiState.Error(
                        e.localizedMessage ?: "Google Sign-In failed (${e.statusCode})",
                    )
            }
        }
    }

    fun resetState() {
        _uiState.value = LoadableUiState.Idle
    }
}