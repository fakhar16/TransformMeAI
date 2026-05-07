package com.fuza.transformmeai.viewmodel

import androidx.lifecycle.ViewModel
import com.fuza.transformmeai.data.session.TransformSessionStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val sessionStore: TransformSessionStore,
) : ViewModel() {
    fun onPhotoCaptured(absolutePath: String) {
        sessionStore.setCapturedImagePath(absolutePath)
    }
}