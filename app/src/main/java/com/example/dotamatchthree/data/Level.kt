package com.example.dotamatchthree.data


// load from database
class Level {
    private var level = arrayOf(
        intArrayOf(7, 8, 9, 10, 4, 3, 1, 2, 4),
        intArrayOf(6, 1, 2, 4, 1, 3, 11, 11, 6),
        intArrayOf(3, 4, 2, 5, 2, 3, 3, 1, 2),
        intArrayOf(2, 1, 3, 3, 1, 5, 4, 5, 4),
        intArrayOf(2, 2, 5, 1, 1, 2, 6, 11, 2),
        intArrayOf(11, 3, 6, 1, 1, 3, 11, 2, 2),
        intArrayOf(3, 6, 4, 2, 2, 4, 1, 11, 11),
        intArrayOf(2, 1, 6, 3, 3, 6, 5, 3, 4),
        intArrayOf(1, 6, 11, 2, 2, 3, 3, 5, 11)
    )

    private var moves = 24
    private var goalType = 3 // Int color
}