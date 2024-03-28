package com.example.dotamatchthree.presentation.ui.game

import android.graphics.Point
import com.example.dotamatchthree.data.Constants
import com.example.dotamatchthree.data.Hero
import com.example.dotamatchthree.data.Level
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class Game {
    lateinit var level: Level

    var board: Array<Array<Hero>> = Array(9) { row ->
        Array(9) { col ->
            Hero(row.toFloat(), col.toFloat(), 0)
        }
    }
    var topBoard: Array<Hero> = Array(9) { row ->
        Hero(0.0f, row.toFloat(), 0)
    }
    val search: ArrayList<ArrayList<Point>> = ArrayList()

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

    var oldX = 0f
    var oldY = 0f
    var posI = 0
    var posJ = 0
    var direction: String? = null
    var newPosI = 0
    var newPosJ = 0

    var move = false
    var swapped = false
    var dropStop = true

    val grid = Array(9) { IntArray(9) { 0 } }


    fun init(lvl: Level) {
        level = lvl

        // create grid
        for (i in grid.indices) {
            for (j in grid[0].indices) {
                grid[i][j] = generateNewJewels()
            }
        }

        setMoves(level.moves)
        setGoal(level.goal)
        setGoalType(level.goalType)

        for (i in grid.indices) {
            for (j in grid.indices) {
                board[i][j] = Hero(
                    Constants.drawX + Constants.cellWidth * j,
                    Constants.drawY + Constants.cellWidth * i,
                    grid[i][j]
                )
            }
        }

    }

    fun swap() {
        when (direction) {
            "right" -> {
                board[posI][posJ + 1].posX -= Constants.cellWidth / 8
                board[posI][posJ].posX += Constants.cellWidth / 8
            }

            "left" -> {
                board[posI][posJ - 1].posX += Constants.cellWidth / 8
                board[posI][posJ].posX -= Constants.cellWidth / 8
            }

            "up" -> {
                board[posI - 1][posJ].posY += Constants.cellWidth / 8
                board[posI][posJ].posY -= Constants.cellWidth / 8
            }

            "down" -> {
                board[posI + 1][posJ].posY -= Constants.cellWidth / 8
                board[posI][posJ].posY += Constants.cellWidth / 8
            }
        }

        val j: Hero = board[posI][posJ]
        board[posI][posJ] = board[newPosI][newPosJ]
        board[newPosI][newPosJ] = j
        board[posI][posJ].posX = ((posJ * Constants.cellWidth + Constants.drawX))
        board[posI][posJ].posY = ((posI * Constants.cellWidth + Constants.drawY))
        board[newPosI][newPosJ].posX = ((newPosJ * Constants.cellWidth + Constants.drawX))
        board[newPosI][newPosJ].posY = ((newPosI * Constants.cellWidth + Constants.drawY))

    }

    fun crush() {
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
    }

    fun fillCrushing() {
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
                            if (board[i][j].color == level.goalType) setGoal(goal.value - 1)
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
                                if (board[i][j].color == level.goalType) setGoal(goal.value - 1)
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

    fun allowCrushing(points: ArrayList<Point>): Boolean {
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

    private fun generateNewJewels(): Int {
        return (level.rangeFrom until level.rangeTo).random()
    }


    fun fillTopBoard() {
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
                topBoard[k].posY += Constants.cellWidth / 8
                if (Constants.drawY.toInt() - topBoard[k].posY < Constants.cellWidth / 8) {
                    board[0][k].color = topBoard[k].color
                    topBoard[k].color = 0
                    topBoard[k].posY = board[0][k].posY - Constants.cellWidth
                    topBoard[k].posX = Constants.drawX.toInt() + k * Constants.cellWidth
                    dropStop = true
                    break
                }
            }
        }

        for (i in 0 until board.size - 1) {
            for (j in board[0].indices) {
                if (board[i][j].color > 0) {
                    if (board[i + 1][j].color == 0) {
                        board[i][j].posY += Constants.cellWidth / 8
                        if (Constants.drawY.toInt() + (i + 1) * Constants.cellWidth - board[i][j].posY < Constants.cellWidth / 8) {
                            board[i + 1][j].color = board[i][j].color
                            board[i][j].color = 0
                            board[i][j].posY = Constants.drawY + i * Constants.cellWidth
                            board[i][j].posX = Constants.drawX + j * Constants.cellWidth
                            dropStop = true
                        }
                    }
                }
            }
        }
    }

}