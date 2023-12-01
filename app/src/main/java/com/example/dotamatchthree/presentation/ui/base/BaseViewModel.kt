package com.example.dotamatchthree.presentation.ui.base

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

interface ViewState

abstract class BaseViewModel<S : ViewState>(initialState: S) : ViewModel() {
    private val _state = mutableStateOf(initialState)
    val state : State<S> = _state

    fun updateState(newState: S) {
        if(newState != _state.value){
            _state.value = newState
        }
    }
}