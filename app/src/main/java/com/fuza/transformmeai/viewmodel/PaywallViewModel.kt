package com.fuza.transformmeai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fuza.transformmeai.data.session.TransformSessionStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaywallViewModel @Inject constructor(
    private val sessionStore: TransformSessionStore,
) : ViewModel() {

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    fun unlockAllLooks(onComplete: () -> Unit) {
        viewModelScope.launch {
            _isProcessing.value = true
            delay(900)
            sessionStore.unlockAllLooks()
            _isProcessing.value = false
            onComplete()
        }
    }
}