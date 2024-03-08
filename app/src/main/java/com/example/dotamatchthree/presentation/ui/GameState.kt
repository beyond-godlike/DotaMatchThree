package com.example.dotamatchthree.presentation.ui

import com.example.dotamatchthree.presentation.ui.base.ViewState


open class GameState : ViewState {
    object IDLE : GameState()
    object SWAPPING : GameState()
    object CHECKSWAPPING : GameState()
    object CRUSHING : GameState()
    object UPDATE : GameState()
    object WIN : GameState()
    object LOSE : GameState()
    data class MESSAGE(val message: String) : GameState()
}