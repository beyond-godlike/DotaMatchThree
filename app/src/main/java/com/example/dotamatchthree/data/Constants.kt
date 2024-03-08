package com.example.dotamatchthree.data

import androidx.compose.ui.unit.IntOffset


object Constants {
    var screenWidth = 0.0f
    var screenHeight = 0.0f
    var cellWidth = 0.0f
    var drawX = 0f
    var drawY = 0f

    val jsz = 32

    val abbaddon = IntOffset(0 , 0)
    val bane = IntOffset(jsz *6, 0)
    val cm = IntOffset(jsz *2, jsz)
    val ds = IntOffset(jsz *3, jsz)
    val dk = IntOffset(jsz *8, jsz)
    val bat = IntOffset(jsz *7, 0)
    val wk = IntOffset(0, jsz *5)
    val pa = IntOffset(jsz *4, jsz)
    val ember = IntOffset(jsz *13, jsz)
    val brood = IntOffset(jsz *13, 0)
    val viper = IntOffset(jsz *6, jsz *6)

    val heroMap = mapOf(
        1 to abbaddon,
        2 to bane,
        3 to cm,
        4 to ds,
        5 to dk,
        6 to bat,
        7 to wk,
        8 to pa,
        9 to ember,
        10 to brood,
        11 to viper
    )
}