package com.example.dotamatchthree.presentation.ui.game

import android.graphics.Point
import com.example.dotamatchthree.data.Constants
import com.example.dotamatchthree.data.Hero
import com.example.dotamatchthree.data.Level
import com.example.dotamatchthree.presentation.ui.MainDispatcherRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GameTest {
    private lateinit var game: Game
    private lateinit var level: Level

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)

        game = Game()
        level = Level(1, 8, 3, 12, 1, 10)
        game.init(level)

    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test setGoal updates goal value`() {
        mainDispatcherRule.run {
            val testGoal = 20
            game.setGoal(testGoal)

            Assert.assertEquals(testGoal, game.goal.value)
        }
    }

    @Test
    fun testGridInitialization() {
        Assert.assertEquals(9, game.grid.size)
        Assert.assertEquals(9, game.grid[0].size)
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
        mainDispatcherRule.runCatching {
            for (i in game.grid.indices) {
                for (j in game.grid[0].indices) {
                    game.grid[i][j] = 0
                }
            }
            assertTrue(game.checkDrop())
        }

    }

    @Test
    fun testCrush() {
        mainDispatcherRule.runCatching {
            game.board[0][0] = Hero(0f, 0f, 1)
            game.board[0][1] = Hero(0f, 1f, 1)
            game.board[0][2] = Hero(0f, 2f, 1)
            game.search.add(arrayListOf(Point(0, 0), Point(0, 1), Point(0, 2)))

            game.crush()
        }

    }

    @Test
    fun testFillTopBoard() {
        mainDispatcherRule.runCatching {
            val hero = Hero(0.0f, 0.0f, 0)
            val board = Array(9) { hero }
            game.fillTopBoard()

            var isBoardFilled = true

            for (i in board.indices) {
                if (board[i].color == 0) {
                    isBoardFilled = false
                    break
                }
            }

            assertTrue(isBoardFilled)

        }
    }

    @Test
    fun testAllowCrushing() {
        mainDispatcherRule.runCatching {
            val point1 = Point(0, 0)
            val point2 = Point(1, 0)
            val point3 = Point(2, 0)
            val point4 = Point(3, 0)

            game.board[point1.x][point1.y] = Hero(point1.x.toFloat(), point1.y.toFloat(), 1)
            game.board[point2.x][point2.y] = Hero(point2.x.toFloat(), point2.y.toFloat(), 1)
            game.board[point3.x][point3.y] = Hero(point3.x.toFloat(), point3.y.toFloat(), 0)
            game.board[point4.x][point4.y] = Hero(point4.x.toFloat(), point4.y.toFloat(), 1)

            val points = arrayListOf(point1, point2, point3, point4)

            Assert.assertFalse(game.allowCrushing(points))
        }
    }
    @Test
    fun testSwap() {
        mainDispatcherRule.runCatching {
            game.posI = 3
            game.posJ = 3
            game.newPosI = 3
            game.newPosJ = 4
            val originalHero = game.board[3][3]
            val targetHero = game.board[3][4]

            game.swap()

            Assert.assertEquals(originalHero.posX + Constants.cellWidth/8, game.board[3][4].posX)
            Assert.assertEquals(originalHero.posY, game.board[3][4].posY)

            Assert.assertEquals(targetHero.posX - Constants.cellWidth/8, game.board[3][3].posX)
            Assert.assertEquals(targetHero.posY, game.board[3][3].posY)

            Assert.assertEquals(targetHero, game.board[3][4])
            Assert.assertEquals(originalHero, game.board[3][3])
        }
    }
    @Test
    fun testDrop() {
        mainDispatcherRule.runCatching {
            val hero1 = Hero(0.0f, 0.0f, 1)
            val hero2 = Hero(0.0f, 1.0f, 2)
            val hero3 = Hero(0.0f, 2.0f, 0)
            val hero4 = Hero(0.0f, 3.0f, 0)

            game.board[0][0] = hero1
            game.board[0][1] = hero2
            game.topBoard[0] = hero3
            game.topBoard[1] = hero4

            game.drop()

            Assert.assertFalse(game.dropStop)

            Assert.assertEquals(1, game.board[0][0].color)
            Assert.assertEquals(0, game.topBoard[0].color)
            Assert.assertEquals(0, game.topBoard[0].posY)

            Assert.assertEquals(2, game.board[1][1].color)
        }
    }

    @Test
    fun testDropWithEmptyTopBoard() {
        mainDispatcherRule.runCatching {
            val hero1 = Hero(0.0f, 0.0f, 1)

            game.board[0][0] = hero1
            game.topBoard[0] = Hero(0.0f, 0.0f, 0)

            game.drop()

            assertTrue(game.dropStop)

            Assert.assertEquals(1, game.board[0][0].color)
            Assert.assertEquals(0, game.topBoard[0].color)
            Assert.assertEquals(0, game.topBoard[0].posY)
        }
    }
}