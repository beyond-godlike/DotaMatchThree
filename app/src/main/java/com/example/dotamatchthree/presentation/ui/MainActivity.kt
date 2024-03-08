@file:OptIn(
    ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class,
    ExperimentalComposeUiApi::class
)

package com.example.dotamatchthree.presentation.ui

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dotamatchthree.data.Constants.abbaddon
import com.example.dotamatchthree.data.Constants.bane
import com.example.dotamatchthree.data.Constants.bat
import com.example.dotamatchthree.data.Constants.brood
import com.example.dotamatchthree.data.Constants.cellWidth
import com.example.dotamatchthree.data.Constants.cm
import com.example.dotamatchthree.data.Constants.dk
import com.example.dotamatchthree.data.Constants.drawX
import com.example.dotamatchthree.data.Constants.drawY
import com.example.dotamatchthree.data.Constants.ds
import com.example.dotamatchthree.data.Constants.ember
import com.example.dotamatchthree.data.Constants.jsz
import com.example.dotamatchthree.data.Constants.pa
import com.example.dotamatchthree.data.Constants.screenHeight
import com.example.dotamatchthree.data.Constants.screenWidth
import com.example.dotamatchthree.data.Constants.viper
import com.example.dotamatchthree.data.Constants.wk
import com.example.dotamatchthree.data.Hero
import com.example.dotamatchthree.R
import com.example.dotamatchthree.data.Constants.heroMap
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlin.math.abs

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current
            val displayMetrics = context.resources.displayMetrics
            screenWidth = displayMetrics.widthPixels.toFloat()
            screenHeight = displayMetrics.heightPixels.toFloat()

            cellWidth = (screenWidth / 9)
            //drawX = ((screenWidth - cellWidth * 9) / 2)
            //drawY = (cellWidth * 4)

            drawX = 0.0f
            drawY = 0.0f

            Column(
                modifier = Modifier
                    .width(screenWidth.dp)
                    .height(screenHeight.dp)
            ) {
                val viewModel: MainViewModel = hiltViewModel()
                Grid(viewModel)
            }
        }
    }
}

@Composable
fun Grid(viewModel: MainViewModel) {
    val bottom = ImageBitmap.imageResource(id = R.drawable.bottom)
    val piece = ImageBitmap.imageResource(id = R.drawable.jewels)

    val mContext = LocalContext.current


    var sec by remember {
        mutableStateOf(0)
    }

    LaunchedEffect(key1 = Unit, block = {
        while (true) {
            delay(30)
            sec += 30
        }
    })

    when (viewModel.state.value) {
        is GameState.WIN -> {
            Column {
                TopPic(viewModel)
                Win(viewModel)
            }
        }

        is GameState.LOSE -> {
            Column {
                TopPic(viewModel)
                Lost(viewModel)
            }
        }

        is GameState.MESSAGE -> {
            Column {
                TopPic(viewModel)
                Box(Modifier.fillMaxSize()) {
                    Text(
                        (viewModel.state.value as GameState.MESSAGE).message,
                        Modifier.padding(50.dp)
                    )
                }
            }
        }
    }
    TopPic(viewModel)
    TopLabel(viewModel)

    Canvas(
        modifier = Modifier
            .width((screenWidth - screenWidth / 5).dp)
            .height((cellWidth * 9).dp)
            .pointerInteropFilter {
                when (it.action) {
                    MotionEvent.ACTION_DOWN -> {
                        viewModel.oldX = it.x
                        viewModel.oldY = it.y
                        viewModel.posI = ((viewModel.oldY - drawY) / cellWidth).toInt()
                        viewModel.posJ = ((viewModel.oldX - drawX) / cellWidth).toInt()
                        viewModel.move = true
                    }

                    MotionEvent.ACTION_MOVE -> {
                        if (viewModel.state.value == GameState.IDLE) {
                            val newX = it.x
                            val newY = it.y
                            val deltaX = abs(newX - viewModel.oldX)
                            val deltaY = abs(newY - viewModel.oldY)

                            if (viewModel.move && (deltaX > 30 || deltaY > 30)) {
                                viewModel.move = false
                                if (abs(viewModel.oldX - newX) > abs(viewModel.oldY - newY)) {
                                    if (newX > viewModel.oldX) {
                                        viewModel.direction = "right"
                                        viewModel.newPosJ = viewModel.posJ + 1
                                    } else {
                                        viewModel.direction = "left"
                                        viewModel.newPosJ = viewModel.posJ - 1
                                    }
                                    viewModel.newPosI = viewModel.posI
                                }
                                if (abs(viewModel.oldY - newY) > abs(viewModel.oldX - newX)) {
                                    if (newY > viewModel.oldY) {
                                        viewModel.direction = "down"
                                        viewModel.newPosI = viewModel.posI + 1
                                    } else {
                                        viewModel.direction = "up"
                                        viewModel.newPosI = viewModel.posI - 1
                                    }
                                    viewModel.newPosJ = viewModel.posJ
                                }
                                // pass human
                                viewModel.updateState(GameState.SWAPPING)
                            }
                        }
                    }
                }
                return@pointerInteropFilter true
            },

        onDraw = {
            for (i in 0..9) {
                for (j in 0..9) {
                    drawLine(
                        start = Offset(x = 0.0f, y = drawY + i * cellWidth),
                        end = Offset(x = cellWidth * 9.0f, y = drawY + i * cellWidth),
                        color = Color.LightGray
                    )

                    drawLine(
                        start = Offset(x = j * cellWidth, y = drawY),
                        end = Offset(x = j * cellWidth, y = drawY + cellWidth * 9),
                        color = Color.LightGray
                    )
                }
            }

            sec.let { _ ->
                drawHeroes(viewModel, piece)
            }
            when (viewModel.state.value) {
                is GameState.IDLE -> {
                }

                is GameState.UPDATE -> {
                    viewModel.updateGame()
                }

                is GameState.CHECKSWAPPING -> {
                    viewModel.updateGame()
                }

                is GameState.SWAPPING -> {
                    viewModel.updateGame()
                }

                is GameState.CRUSHING -> {
                    viewModel.updateGame()
                }
            }


            drawImage(
                image = bottom,
                srcOffset = IntOffset(0, 0),
                srcSize = IntSize(1834, 979),
                dstOffset = IntOffset(60, (drawY.toInt() + cellWidth.toInt() * 10)),
                dstSize = IntSize((screenWidth - screenWidth / 5).toInt(), (cellWidth * 4).toInt())
            )


        },
        contentDescription = ""
    )
}

@Composable
fun Win(viewModel: MainViewModel) {
    Box(Modifier.fillMaxSize()) {
        Text("WON", Modifier.padding(50.dp))
        TextButton(
            modifier = Modifier.padding(100.dp),
            onClick = {
                viewModel.incLevel()
                viewModel.newGame()
            })
        {
            Text(text = "Go next", Modifier.padding(50.dp))
        }
    }
}

@Composable
fun Lost(viewModel: MainViewModel) {
    Box(Modifier.fillMaxSize()) {
        Text("LOST", Modifier.padding(50.dp, 50.dp))
        TextButton(
            modifier = Modifier.padding(100.dp),
            onClick = { viewModel.newGame() })
        {
            Text(text = "Try again", Modifier.padding(100.dp))
        }
    }
}

@Composable
fun TopLabel(viewModel: MainViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp, 0.dp)
    ) {
        val subBitmap = imageBitmap(viewModel.level!!.goalType)
        val moves = viewModel.moves.collectAsState()
        val goal = viewModel.goal.collectAsState()

        Text(text = moves.value.toString(), Modifier.padding(0.dp, 10.dp), fontSize = 32.sp)
        Spacer(modifier = Modifier.padding(90.dp, 0.dp))
        Image(
            bitmap = subBitmap, contentDescription = "",
            Modifier
                .padding(0.dp, 8.dp)
                .size(30.dp)
        )
        Text(text = "x " + goal.value.toString(), Modifier.padding(16.dp, 10.dp), fontSize = 16.sp)
    }
}

@Composable
private fun imageBitmap(type: Int): ImageBitmap {
    val piece = ImageBitmap.imageResource(id = R.drawable.jewels)
    val hero = heroMap[type]!!

    val subBitmap = piece.asAndroidBitmap().let {
        Bitmap.createBitmap(it, hero.x, hero.y, jsz, jsz)
    }.asImageBitmap()
    return subBitmap
}

@Composable
fun TopPic(viewModel: MainViewModel) {

    TextButton(
        onClick = { viewModel.newGame() },
        modifier = Modifier
            .width(379.dp)
            .height(133.dp),
        shape = RectangleShape
    ) {
        Image(
            painter = painterResource(R.drawable.top),
            contentDescription = "",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
    }
}

private fun DrawScope.drawHeroes(
    viewModel: MainViewModel,
    piece: ImageBitmap
) {
    for (heroes in viewModel.board) {
        for (hero in heroes) {
            heroMap[hero.color]?.let { drawHero(piece, hero, it) }
        }
    }
}

private fun DrawScope.drawHero(
    piece: ImageBitmap,
    hero: Hero,
    srcOffset: IntOffset
) {
    drawImage(
        image = piece,
        srcOffset = srcOffset,
        srcSize = IntSize(jsz, jsz),
        dstOffset = IntOffset(hero.posX.toInt(), hero.posY.toInt()),
        dstSize = IntSize(cellWidth.toInt(), cellWidth.toInt())
    )
}

private fun mToast(context: Context, msg: String) {
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
}