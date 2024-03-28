package com.example.dotamatchthree.presentation.ui.game

import android.content.Context
import com.example.dotamatchthree.data.Level
import com.example.dotamatchthree.data.api.dao.LevelDao
import com.example.dotamatchthree.domain.PrefsHelper
import com.example.dotamatchthree.presentation.ui.MainDispatcherRule
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class GameViewModelTest {
    @Mock
    private lateinit var context: Context

    @Mock
    private lateinit var db: LevelDao

    @Mock
    private lateinit var prefsHelper: PrefsHelper

    @Mock
    private lateinit var game: Game

    private lateinit var viewModel: GameViewModel

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    @Before
    fun setUp() {
        viewModel = GameViewModel(context, db, prefsHelper, game)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun testInitialState() {
        mainDispatcherRule.runCatching {
            Assert.assertEquals(GameState.UPDATE, viewModel.state.value)
        }
    }

    @Test
    fun testNewGame() {
        mainDispatcherRule.runCatching {
            viewModel.newGame()
            Assert.assertEquals(GameState.UPDATE, viewModel.state.value)
        }
    }

    @Test
    fun checkWin_movesEqualToZero_goalGreaterThanZero_updateStateLoseCalled() {
        mainDispatcherRule.runCatching {
            viewModel.game.setMoves(0)
            viewModel.game.setGoal(20)

            viewModel.checkWin()
            Assert.assertEquals(GameState.LOSE, viewModel.state.value)
        }
    }

    @Test
    fun checkWin_movesGreaterThanZero_goalEqualToZero_updateStateWinCalled() {
        mainDispatcherRule.runCatching {
            game.setMoves(10)
            game.setGoal(0)
            viewModel.checkWin()

            Assert.assertEquals(GameState.WIN, viewModel.state.value)
        }
    }
    @Test
    fun testUpdateGame() {
        mainDispatcherRule.runCatching {
            viewModel.updateState(GameState.SWAPPING)
            viewModel.updateGame()

            Assert.assertEquals(GameState.CHECKSWAPPING, viewModel.state.value)
        }
    }
    @Test
    fun testCheckFurther() {
        mainDispatcherRule.runCatching {
        viewModel.updateState(GameState.SWAPPING)

        viewModel.checkFurther()
        Assert.assertEquals(viewModel.game.moves.value - 1, 5)
        }
    }

    @Test
    fun testIncLevel() {
        mainDispatcherRule.runCatching {
            `when`(prefsHelper.getLevel()).thenReturn(1)
            viewModel.incLevel()

            Assert.assertEquals(prefsHelper.getLevel(), 2)
        }
    }

    @Test
    fun testLoadLevel() {
        mainDispatcherRule.runCatching {
            `when`(prefsHelper.getLevel()).thenReturn(1)

            val testLevel = Level(1, 8, 3, 10, 2, 7)
            `when`(db.getLevel(1)).thenReturn(testLevel)

            val loadedLevel = viewModel.loadLevel()

            Assert.assertEquals(testLevel, loadedLevel)
        }
    }

}