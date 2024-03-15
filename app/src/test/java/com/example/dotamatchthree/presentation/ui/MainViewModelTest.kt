package com.example.dotamatchthree.presentation.ui

import android.content.Context
import com.example.dotamatchthree.data.LevelDao
import com.example.dotamatchthree.domain.PrefsHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock


@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private lateinit var viewModel: MainViewModel
    private lateinit var context: Context
    private lateinit var db: LevelDao
    private lateinit var prefsHelper: PrefsHelper

    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)

        context = mock(Context::class.java)
        db = mock(LevelDao::class.java)
        prefsHelper = mock(PrefsHelper::class.java)

        viewModel = MainViewModel(context, db, prefsHelper)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialState() {
        assertEquals(GameState.UPDATE, viewModel.state.value)
    }
    @Test
    fun `test setGoal updates goal value`()
    {
        val testGoal = 20
        viewModel.setGoal(testGoal)

        assertEquals(testGoal, viewModel.goal.value)
    }

    @Test
    fun testGridInitialization() {
        assertEquals(9, viewModel.grid.size)
        assertEquals(9, viewModel.grid[0].size)
        assertEquals(0, viewModel.grid[0][0])
    }

    @Test
    fun testGenerateNewJewels() {
        val rangeFrom = 1
        val rangeTo = 10
        val result = (rangeFrom until rangeTo).random()

        assertTrue(result >= rangeFrom)
        assertTrue(result < rangeTo)
    }

    @Test
    fun testCheckDrop() {
        for (i in viewModel.grid.indices) {
            for (j in viewModel.grid[0].indices) {
                viewModel.grid[i][j] = 0
            }
        }

        assertTrue(viewModel.checkDrop())
    }


    @Test
    fun checkWin_movesEqualToZero_goalGreaterThanZero_updateStateLoseCalled() {
        viewModel.setMoves(0)
        viewModel.setGoal(20)

        viewModel.checkWin()
        assertEquals(GameState.LOSE, viewModel.state.value)
    }
    @Test
    fun checkWin_movesGreaterThanZero_goalEqualToZero_updateStateWinCalled() {
        viewModel.setMoves(10)
        viewModel.setGoal(0)
        viewModel.checkWin()

        assertEquals(GameState.WIN, viewModel.state.value)
    }


}