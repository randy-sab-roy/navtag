package com.glados.navtag.ui.navtag

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NavTagViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is NavTag Fragment"
    }
    val text: LiveData<String> = _text
}