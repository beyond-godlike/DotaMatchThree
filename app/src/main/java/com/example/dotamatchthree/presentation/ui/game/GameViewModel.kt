package com.example.dotamatchthree.presentation.ui.game

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.dotamatchthree.data.Constants
import com.example.dotamatchthree.data.Level
import com.example.dotamatchthree.data.api.dao.LevelDao
import com.example.dotamatchthree.domain.PrefsHelper
import com.example.dotamatchthree.presentation.ui.base.BaseViewModel
import com.example.dotamatchthree.presentation.ui.base.ViewEvent
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    val context: Context,
    val db: LevelDao,
    val prefsHelper: PrefsHelper,
    val game: Game
) : BaseViewModel<GameState, ViewEvent>(
    initialState = GameState.IDLE
) {
    init {
        // if first run set level to 1
        prefsHelper.saveLevel(1)
        createDb()

        game.init(loadLevel())
    }

    private fun newGame() {
        updateState(GameState.IDLE)

        game.init(loadLevel())

        updateState(GameState.UPDATE)

    }

    fun dispatchEvent(event: Event) {
        when (event) {
            is Event.NewGame -> newGame()
            is Event.Update -> updateGame()
            is Event.Win -> {
                incLevel()
                newGame()
            }
            is Event.Lost -> { newGame() }
            is Event.UpdateState -> updateState(event.state)
        }
    }

    private fun updateGame() {
        when (state.value) {
            GameState.SWAPPING -> {
                game.swap()
                checkFurther()
            }

            GameState.CHECKSWAPPING -> {
                game.fillCrushing()
                if (game.search.isEmpty()) {
                    game.swap()
                    checkFurther()
                } else updateState(GameState.CRUSHING)
            }

            GameState.CRUSHING -> {
                game.crush()

                if (game.search.isEmpty()) {
                    updateState(GameState.UPDATE)
                }
            }

            GameState.UPDATE -> {
                checkWin()

                game.drop()
                game.fillTopBoard()
                game.fillCrushing()
                if (game.search.isEmpty()) {
                    if (!game.checkDrop()) {
                        updateState(GameState.IDLE)
                    }
                } else {
                    updateState(GameState.CRUSHING)
                }
                game.dropStop = false
            }

            GameState.IDLE -> {}

        }
    }

    fun checkFurther() {
        if (state.value == GameState.SWAPPING) {
            updateState(GameState.CHECKSWAPPING)
            // if swapped
            if (game.swapped) {
                game.setMoves(game.moves.value - 1)
                game.swapped = false
            }
        }
        // not swapped
        else {
            game.setMoves(game.moves.value + 1)
            updateState(GameState.IDLE)
        }

        checkWin()
    }

    fun checkWin() {
        if (game.moves.value <= 0) {
            if (game.goal.value > 0) updateState(GameState.LOSE)
        }
        if (game.goal.value <= 0) updateState(GameState.WIN)

    }

    fun jsonString(path: String): String {
        return context.assets.open(path)
            .bufferedReader()
            .use { it.readText() }
    }

    fun incLevel() {
        val l = prefsHelper.getLevel() + 1
        prefsHelper.saveLevel(l)
    }

    fun loadLevel(): Level {
        val currentLevel = prefsHelper.getLevel()
        return db.getLevel(currentLevel)
    }

    // todo only when 1st time
    fun createDb() {
        viewModelScope.launch {
            try {
                val list = object : TypeToken<List<Level>>() {}.type
                val lvls: List<Level> = Gson().fromJson(jsonString(Constants.jsonPath), list)

                db.insertLevels(lvls)
            } catch (e: Exception) {
                updateState(GameState.MESSAGE("not added"))
            }
        }
    }
}

sealed class Event : ViewEvent {
    //data class NewGame(val level: Int) : Event()
    object NewGame : Event()
    object Win : Event()
    object Lost : Event()
    object Update : Event()
    data class UpdateState(val state: GameState) : Event()

}