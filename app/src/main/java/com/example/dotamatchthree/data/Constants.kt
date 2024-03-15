package com.example.dotamatchthree.data

import androidx.compose.ui.unit.IntOffset


object Constants {
    var screenWidth = 0.0f
    var screenHeight = 0.0f
    var cellWidth = 0.0f
    var drawX = 0f
    var drawY = 0f

    //val jsz = 32
    val jsz = 184

    val jsonPath = "lvls.json"

    val slark = IntOffset(0 , 0)
    val mirana = IntOffset(0, jsz)
    val venge = IntOffset(jsz *2, jsz)
    val naga = IntOffset(jsz*2, 0)
    val tide = IntOffset(jsz, 0)
    val wr = IntOffset(jsz *2, jsz *4)
    val shaker = IntOffset(0, jsz *2)
    val pa = IntOffset(jsz *2, jsz*5)
    val mk = IntOffset(jsz * 3, jsz)
    val jugg = IntOffset(jsz *3, jsz *3)
    val am = IntOffset(jsz *3, jsz *4)

    val heroMap = mapOf(
        1 to slark,
        2 to mirana,
        3 to venge,
        4 to naga,
        5 to tide,
        6 to wr,
        7 to shaker,
        8 to pa,
        9 to mk,
        10 to jugg,
        11 to am
    )
}