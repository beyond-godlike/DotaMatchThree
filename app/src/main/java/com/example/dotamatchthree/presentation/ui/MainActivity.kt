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
import com.example.dotamatchthree.R
import com.example.dotamatchthree.data.Constants.cellWidth
import com.example.dotamatchthree.data.Constants.drawX
import com.example.dotamatchthree.data.Constants.drawY
import com.example.dotamatchthree.data.Constants.heroMap
import com.example.dotamatchthree.data.Constants.jsz
import com.example.dotamatchthree.data.Constants.screenHeight
import com.example.dotamatchthree.data.Constants.screenWidth
import com.example.dotamatchthree.data.Hero
import com.example.dotamatchthree.presentation.ui.game.Event
import com.example.dotamatchthree.presentation.ui.game.Game
import com.example.dotamatchthree.presentation.ui.game.GameState
import com.example.dotamatchthree.presentation.ui.game.GameViewModel
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
                val viewModel: GameViewModel = hiltViewModel()
                Grid(
                    state = viewModel.state.value,
                    game = viewModel.game,
                    onEvent = { event -> viewModel.dispatchEvent(event) }
                )
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Grid(
    state: GameState,
    game: Game,
    onEvent: (Event) -> Unit,
) {
    val bottom = ImageBitmap.imageResource(id = R.drawable.bottom)
    val piece = ImageBitmap.imageResource(id = R.drawable.jq)

    var elapsedMillis by remember {
        mutableStateOf(0L)
    }

    LaunchedEffect(key1 = Unit) {
        while (true) {
            delay(30)
            elapsedMillis += 30
        }
    }

    GameStateScreen(state, onEvent)
    TopLabel(game)

    Canvas(
        modifier = Modifier
            .width((screenWidth - screenWidth / 5).dp)
            .height((cellWidth * 9).dp)
            .pointerInteropFilter {
                when (it.action) {
                    MotionEvent.ACTION_DOWN -> {
                        with(game) {
                            oldX = it.x
                            oldY = it.y
                            posI = ((oldY - drawY) / cellWidth).toInt()
                            posJ = ((oldX - drawX) / cellWidth).toInt()
                            move = true
                        }
                    }

                    MotionEvent.ACTION_MOVE -> {
                        if (state == GameState.IDLE) {
                            val newX = it.x
                            val newY = it.y
                            val deltaX = abs(newX - game.oldX)
                            val deltaY = abs(newY - game.oldY)

                            if (game.move && (deltaX > 30 || deltaY > 30)) {
                                game.move = false
                                game.direction = when {
                                    deltaX > deltaY -> if (newX > game.oldX) "right" else "left"
                                    else -> if (newY > game.oldY) "down" else "up"
                                }

                                // Calculate new position based on direction
                                when (game.direction) {
                                    "right" -> {
                                        game.newPosJ = game.posJ + 1
                                        game.newPosI = game.posI
                                    }

                                    "left" -> {
                                        game.newPosJ = game.posJ - 1
                                        game.newPosI = game.posI
                                    }

                                    "down" -> {
                                        game.newPosI = game.posI + 1
                                        game.newPosJ = game.posJ
                                    }

                                    "up" -> {
                                        game.newPosI = game.posI - 1
                                        game.newPosJ = game.posJ
                                    }
                                }

                                onEvent.invoke(Event.UpdateState(GameState.SWAPPING))
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

            drawHeroes(game, piece, elapsedMillis)

            when (state) {
                is GameState.IDLE -> {

                }

                is GameState.UPDATE,
                is GameState.CHECKSWAPPING,
                is GameState.SWAPPING,
                is GameState.CRUSHING -> {
                    onEvent.invoke(Event.Update)
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
fun GameStateScreen(state: GameState, onEvent: (Event) -> Unit) {
    Column {
        TopPic(onEvent)
        when (state) {
            is GameState.WIN -> Win(onEvent)
            is GameState.LOSE -> Lost(onEvent)
            is GameState.MESSAGE -> MessageContent(state.message)
        }
    }
}

@Composable
fun MessageContent(message: String) {
    Box(Modifier.fillMaxSize()) {
        Text(
            message,
            Modifier.padding(50.dp)
        )
    }
}

@Composable
fun Win(onEvent: (Event) -> Unit) {
    Box(Modifier.fillMaxSize()) {
        Text("WON", Modifier.padding(50.dp))
        TextButton(
            modifier = Modifier.padding(100.dp),
            onClick = {
                onEvent.invoke(Event.Win)
            })
        {
            Text(text = "Go next", Modifier.padding(50.dp))
        }
    }
}

@Composable
fun Lost(onEvent: (Event) -> Unit) {
    Box(Modifier.fillMaxSize()) {
        Text("LOST", Modifier.padding(50.dp, 50.dp))
        TextButton(
            modifier = Modifier.padding(100.dp),
            onClick = { onEvent.invoke(Event.Lost) })
        {
            Text(text = "Try again", Modifier.padding(100.dp))
        }
    }
}

@Composable
fun TopLabel(game: Game) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp, 0.dp)
    ) {
        val subBitmap = imageBitmap(game.level.goalType)
        val moves = game.moves.collectAsState()
        val goal = game.goal.collectAsState()

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
    val piece = ImageBitmap.imageResource(id = R.drawable.jq)
    val hero = heroMap[type]!!

    val subBitmap = piece.asAndroidBitmap().let {
        Bitmap.createBitmap(it, hero.x, hero.y, jsz, jsz)
    }.asImageBitmap()
    return subBitmap
}

@Composable
fun TopPic(onEvent: (Event) -> Unit) {

    TextButton(
        onClick = { onEvent.invoke(Event.NewGame) },
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
    game: Game,
    piece: ImageBitmap,
    elapsedMillis: Long
) {
    elapsedMillis.let {
        for (heroes in game.board) {
            for (hero in heroes) {
                heroMap[hero.color]?.let { drawHero(piece, hero, it) }
            }
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