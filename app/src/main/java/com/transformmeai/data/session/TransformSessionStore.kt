package com.transformmeai.data.session

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

data class TransformSessionState(
    val isLoggedIn: Boolean = false,
    val capturedImagePath: String? = null,
    val generatedLookUrls: List<String> = emptyList(),
    val allLooksUnlocked: Boolean = false,
)

@Singleton
class TransformSessionStore @Inject constructor() {
    private val _state = MutableStateFlow(TransformSessionState())
    val state: StateFlow<TransformSessionState> = _state.asStateFlow()

    fun setLoggedIn(value: Boolean) {
        _state.update { it.copy(isLoggedIn = value) }
    }

    fun setCapturedImagePath(path: String) {
        _state.update { it.copy(capturedImagePath = path) }
    }

    fun setGeneratedLooks(urls: List<String>) {
        _state.update {
            it.copy(
                generatedLookUrls = urls,
                allLooksUnlocked = false,
            )
        }
    }

    fun unlockAllLooks() {
        _state.update { it.copy(allLooksUnlocked = true) }
    }

    fun resetCapturePipeline() {
        _state.update {
            it.copy(
                capturedImagePath = null,
                generatedLookUrls = emptyList(),
                allLooksUnlocked = false,
            )
        }
    }
}
