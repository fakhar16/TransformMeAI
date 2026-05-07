package com.fuza.transformmeai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fuza.transformmeai.data.session.TransformSessionStore
import com.fuza.transformmeai.domain.model.LoadableUiState
import com.fuza.transformmeai.domain.repository.TransformRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val repository: TransformRepository,
    private val sessionStore: TransformSessionStore,
) : ViewModel() {

    val sessionState = sessionStore.state

    private val _downloadState = MutableStateFlow<LoadableUiState<String>>(LoadableUiState.Idle)
    val downloadState: StateFlow<LoadableUiState<String>> = _downloadState.asStateFlow()

    fun downloadUnlockedLook(url: String) {
        viewModelScope.launch {
            _downloadState.value = LoadableUiState.Loading
            val result = repository.downloadRemoteImageToPictures(url)
            result.fold(
                onSuccess = { path ->
                    _downloadState.value = LoadableUiState.Success(path)
                },
                onFailure = { error ->
                    _downloadState.value =
                        LoadableUiState.Error(
                            error.localizedMessage ?: "Download failed",
                        )
                },
            )
        }
    }

    fun consumeDownloadMessage() {
        _downloadState.value = LoadableUiState.Idle
    }

    fun startNewSession() {
        sessionStore.resetCapturePipeline()
    }
}