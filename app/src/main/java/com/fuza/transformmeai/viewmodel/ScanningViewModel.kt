package com.fuza.transformmeai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fuza.transformmeai.data.session.TransformSessionStore
import com.fuza.transformmeai.domain.model.LoadableUiState
import com.fuza.transformmeai.domain.repository.TransformRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ScanningViewModel @Inject constructor(
    private val repository: TransformRepository,
    private val sessionStore: TransformSessionStore,
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoadableUiState<Unit>>(LoadableUiState.Idle)
    val uiState: StateFlow<LoadableUiState<Unit>> = _uiState.asStateFlow()

    fun startPipeline() {
        viewModelScope.launch {
            _uiState.value = LoadableUiState.Loading
            val path =
                sessionStore.state.value.capturedImagePath
            if (path.isNullOrBlank()) {
                _uiState.value = LoadableUiState.Error("Missing photo. Please capture again.")
                return@launch
            }

            delay(2_400)
            val result = repository.generateLooks(File(path))
            result.fold(
                onSuccess = { urls ->
                    sessionStore.setGeneratedLooks(urls)
                    _uiState.value = LoadableUiState.Success(Unit)
                },
                onFailure = { error ->
                    _uiState.value =
                        LoadableUiState.Error(
                            error.localizedMessage ?: "Generation failed",
                        )
                },
            )
        }
    }

    fun acknowledgeError() {
        _uiState.value = LoadableUiState.Idle
    }
}