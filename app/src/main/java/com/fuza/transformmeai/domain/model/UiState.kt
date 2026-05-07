package com.fuza.transformmeai.domain.model

sealed interface LoadableUiState<out T> {
    data object Idle : LoadableUiState<Nothing>
    data object Loading : LoadableUiState<Nothing>
    data class Success<T>(val data: T) : LoadableUiState<T>
    data class Error(val message: String) : LoadableUiState<Nothing>
}