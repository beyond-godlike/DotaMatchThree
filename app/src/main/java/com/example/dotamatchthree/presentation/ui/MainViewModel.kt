package com.example.dotamatchthree.presentation.ui

import com.example.dotamatchthree.presentation.ui.base.BaseViewModel
import com.example.dotamatchthree.presentation.ui.game.GameState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
) : BaseViewModel<GameState>(
    initialState = GameState.IDLE
) {
}
