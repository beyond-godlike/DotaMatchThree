package com.example.dotamatchthree.presentation.ui

import android.content.Context
import android.graphics.Point
import androidx.lifecycle.viewModelScope
import com.example.dotamatchthree.data.Constants.cellWidth
import com.example.dotamatchthree.data.Constants.drawX
import com.example.dotamatchthree.data.Constants.drawY
import com.example.dotamatchthree.data.Constants.jsonPath
import com.example.dotamatchthree.data.Hero
import com.example.dotamatchthree.data.Level
import com.example.dotamatchthree.data.api.dao.LevelDao
import com.example.dotamatchthree.domain.PrefsHelper
import com.example.dotamatchthree.presentation.ui.base.BaseViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val context: Context,
    private val db: LevelDao,
    private val prefsHelper: PrefsHelper,
) : BaseViewModel<GameState>(
    initialState = GameState.IDLE
) {

    var board: Array<Array<Hero>> = Array(9) { row ->
        Array(9) { col ->
            Hero(row.toFloat(), col.toFloat(), 0)
        }
    }
    var topBoard: Array<Hero> = Array(9) { row ->
        Hero(0.0f, row.toFloat(), 0)
    }
    private val search: ArrayList<ArrayList<Point>> = ArrayList()

    var oldX = 0f
    var oldY = 0f
    var posI = 0
    var posJ = 0
    var direction: String? = null
    var newPosI = 0
    var newPosJ = 0

    var move = false
    var swapped = false
    private var swapIndex = 3
    private var dropStop = true

    var level: Level? = null
    val grid = Array(9) { IntArray(9) { 0 } }

    private val _moves = MutableStateFlow(10)
    val moves = _moves.asStateFlow()

    private val _goal = MutableStateFlow(10)
    val goal = _goal.asStateFlow()

    private val _goalType = MutableStateFlow(1)
    val goalType = _goalType.asStateFlow()

    fun setMoves(moves: Int) {
        _moves.value = moves
    }

    fun setGoal(goal: Int) {
        _goal.value = goal
    }

    private fun setGoalType(goalType: Int) {
        _goalType.value = goalType
    }

    init {
        // if first run set level to 1
        prefsHelper.saveLevel(1)
        createDb()
        newGame()
    }

    // todo only when 1st time
    private fun createDb() {
        viewModelScope.launch {
            try {
                val list = object : TypeToken<List<Level>>() {}.type
                val lvls: List<Level> = Gson().fromJson(jsonString(jsonPath), list)

                db.insertLevels(lvls)
            } catch (e: Exception) {
                updateState(GameState.MESSAGE("not added"))
            }
        }
    }

    private fun jsonString(path: String): String {
        return context.assets.open(path)
            .bufferedReader()
            .use { it.readText() }
    }
    fun incLevel() {
        val l = prefsHelper.getLevel() + 1
        prefsHelper.saveLevel(l)
    }

    private fun loadLevel() {
        val currentLevel = prefsHelper.getLevel()
        level = db.getLevel(currentLevel)
    }

    fun newGame() {
        updateState(GameState.IDLE)
        loadLevel()

        if (level != null) {

            // create grid
            for (i in grid.indices) {
                for (j in grid[0].indices) {
                    grid[i][j] = generateNewJewels()
                }
            }


            setMoves(level!!.moves)
            setGoal(level!!.goal)
            setGoalType(level!!.goalType)

            for (i in grid.indices) {
                for (j in grid.indices) {
                    board[i][j] = Hero(
                        drawX + cellWidth * j,
                        drawY + cellWidth * i,
                        grid[i][j]
                    )
                }
            }

        }

        updateState(GameState.UPDATE)
    }

    private fun generateNewJewels(): Int {
        return (level!!.rangeFrom until level!!.rangeTo).random()
    }


    fun updateGame() {
        when (state.value) {
            GameState.SWAPPING -> {
                swap()
            }

            GameState.CHECKSWAPPING -> {
                fillCrushing()
                if (search.isEmpty()) {
                    swap()
                } else updateState(GameState.CRUSHING)
            }


            GameState.CRUSHING -> {
                var i = 0
                while (i < search.size) {
                    var j = 0
                    while (j < search[i].size) {
                        board[search[i][j].x][search[i][j].y].color = 0
                        j++
                    }
                    search.removeAt(i)
                    i--
                    i++
                }

                if (search.isEmpty()) {
                    updateState(GameState.UPDATE)
                }
            }

            GameState.UPDATE -> {
                checkWin()

                drop()
                fillTopBoard()
                fillCrushing()
                if (search.isEmpty()) {
                    if (!checkDrop()) {
                        updateState(GameState.IDLE)
                    }
                } else {
                    updateState(GameState.CRUSHING)
                }
                dropStop = false
            }

            GameState.IDLE -> {}
        }
    }

    fun checkWin() {
        if (moves.value <= 0) {
            if (goal.value > 0) updateState(GameState.LOSE)
        }
        if (goal.value <= 0) updateState(GameState.WIN)

    }

    private fun swap() {
        if (swapIndex > 0) {
            when (direction) {
                "right" -> {
                    board[posI][posJ + 1].posX -= cellWidth / 8
                    board[posI][posJ].posX += cellWidth / 8
                }

                "left" -> {
                    board[posI][posJ - 1].posX += cellWidth / 8
                    board[posI][posJ].posX -= cellWidth / 8
                }

                "up" -> {
                    board[posI - 1][posJ].posY += cellWidth / 8
                    board[posI][posJ].posY -= cellWidth / 8
                }

                "down" -> {
                    board[posI + 1][posJ].posY -= cellWidth / 8
                    board[posI][posJ].posY += cellWidth / 8
                }
            }
            //
            swapIndex--
        } else {
            val j: Hero = board[posI][posJ]
            board[posI][posJ] = board[newPosI][newPosJ]
            board[newPosI][newPosJ] = j
            board[posI][posJ].posX = ((posJ * cellWidth + drawX))
            board[posI][posJ].posY = ((posI * cellWidth + drawY))
            board[newPosI][newPosJ].posX = ((newPosJ * cellWidth + drawX))
            board[newPosI][newPosJ].posY = ((newPosI * cellWidth + drawY))
            swapIndex = 8
            if (state.value == GameState.SWAPPING) {
                updateState(GameState.CHECKSWAPPING)
                // if swapped
                if (swapped) {
                    setMoves(moves.value - 1)
                    swapped = false
                }
            }
            // not swapped
            else {
                setMoves(moves.value + 1)
                updateState(GameState.IDLE)
            }
        }

        checkWin()
    }

    private fun fillCrushing() {
        search.clear()
        for (i in board.indices) {
            var j = 0
            while (j < board[0].size) {
                if (board[i][j].color > 0) {
                    var k = j + 1
                    while (k < board.size && board[i][k].color == board[i][j].color) {
                        k++
                    }
                    if (k - j >= 3) {
                        for (m in j until k) {
                            search.add(ArrayList())
                            search[search.size - 1].add(Point(i, m))
                            if (board[i][j].color == level!!.goalType) setGoal(goal.value - 1)
                        }
                    }
                }
                // setSwapped
                swapped = true
                j++
            }

        }
        run {
            var i = 0
            while (i < board.size) {
                var j = 0
                while (j < board[0].size) {
                    if (board[i][j].color > 0) {
                        var k = 0
                        while (i + k < board.size && board[i][j].color == board[i + k][j].color) {
                            k++
                        }
                        if (k >= 3) {
                            search.add(ArrayList())
                            for (m in 0 until k) {
                                search[search.size - 1].add(Point(i + m, j))
                                if (board[i][j].color == level!!.goalType) setGoal(goal.value - 1)
                            }
                            i += k - 1
                        }
                        // setSwapped
                        swapped = true
                    }
                    j++
                }
                i++
            }
        }
        var i = 0
        while (i < search.size) {
            if (!allowCrushing(search[i])) {
                search.removeAt(i)
                i--
            }
            i++
        }
    }

    private fun allowCrushing(points: ArrayList<Point>): Boolean {
        var allow = true
        for (i in 0 until points.size) {
            if (points[i].x < board.size - 1) {
                if (board[points[i].x + 1][points[i].y].color == 0) allow = false
            }
        }
        return allow
    }

    fun checkDrop(): Boolean {
        var drop = false
        for (jewels in board) {
            for (j in jewels) {
                if (j.color == 0) {
                    drop = true
                    break
                }
            }
        }
        return drop
    }

    private fun fillTopBoard() {
        for (j in topBoard.indices) {
            if (topBoard[j].color == 0) {
                topBoard[j].color = generateNewJewels()
                if (j > 0) {
                    if (topBoard[j].color == topBoard[j - 1].color) {
                        topBoard[j].color = topBoard[j].color % 11 + 1
                    }
                }
            }
        }
    }

    fun drop() {
        for (k in topBoard.indices) {
            if (board[0][k].color == 0) {
                topBoard[k].posY += cellWidth / 8
                if (drawY.toInt() - topBoard[k].posY < cellWidth / 8) {
                    board[0][k].color = topBoard[k].color
                    topBoard[k].color = 0
                    topBoard[k].posY = board[0][k].posY - cellWidth
                    topBoard[k].posX = drawX.toInt() + k * cellWidth
                    dropStop = true
                    break
                }
            }
        }

        for (i in 0 until board.size - 1) {
            for (j in board[0].indices) {
                if (board[i][j].color > 0) {
                    if (board[i + 1][j].color == 0) {
                        board[i][j].posY += cellWidth / 8
                        if (drawY.toInt() + (i + 1) * cellWidth - board[i][j].posY < cellWidth / 8) {
                            board[i + 1][j].color = board[i][j].color
                            board[i][j].color = 0
                            board[i][j].posY = drawY + i * cellWidth
                            board[i][j].posX = drawX + j * cellWidth
                            dropStop = true
                        }
                    }
                }
            }
        }
    }


}
