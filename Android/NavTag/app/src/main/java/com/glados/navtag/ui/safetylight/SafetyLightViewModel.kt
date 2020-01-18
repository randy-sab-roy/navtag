package com.glados.navtag.ui.safetylight

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SafetyLightViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Press the "
    }
    val text: LiveData<String> = _text
}