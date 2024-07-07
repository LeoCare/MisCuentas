package com.app.miscuentas.features

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class MainActivityViewModel @Inject constructor() : ViewModel() {
    private val _title = MutableStateFlow("Inicio")
    val title: StateFlow<String> = _title

    fun setTitle(newTitle: String) {
        _title.value = newTitle
    }
}