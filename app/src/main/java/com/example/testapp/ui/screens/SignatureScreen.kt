package com.example.testapp.ui.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.testapp.ui.viewmodels.SignatureScreenViewModel
import com.example.testapp.utils.toBase64

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignatureScreen(
    onBackClickListener: () -> Unit,
    viewModel: SignatureScreenViewModel = hiltViewModel()
) {
    var signatureBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    val currentPath = remember { mutableStateOf(Path()) }
    val pathHistory = remember { mutableStateListOf<Path>() }
    val undonePaths = remember { mutableStateListOf<Path>() }
    val density = LocalDensity.current
    val tempPath = Path()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Подпись",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )
                },

                navigationIcon = {
                    IconButton(
                        onClick = {
                            onBackClickListener()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .size(300.dp)
                    .background(MaterialTheme.colorScheme.background)
                    .dashedBorder(1.dp, MaterialTheme.colorScheme.primary, 8.dp)
                    .pointerInput(true) {
                        detectDragGestures(
                            onDragStart = { offset ->


                                undonePaths.clear()
                                currentPath.value = Path().apply {
                                    moveTo(offset.x, offset.y)
                                }

                            },
                            onDrag = { change, dragAmount ->
                                currentPath.value.lineTo(
                                    change.position.x,
                                    change.position.y
                                )
                            },
                            onDragEnd = {
                                pathHistory.add(currentPath.value)
                                currentPath.value = Path()
                            }
                        )
                    }
            ) {
                // Рисуем все сохраненные пути
                pathHistory.forEach { path ->
                    drawPath(
                        path = path,
                        color = Color.Black,
                        style = Stroke(
                            width = 2.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
                // Рисуем текущий путь
                drawPath(
                    path = currentPath.value,
                    color = Color.Black,
                    style = Stroke(
                        width = 2.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        if (pathHistory.isNotEmpty()) {
                            undonePaths.add(pathHistory.removeAt(pathHistory.lastIndex))
                        }
                    },
                    enabled = pathHistory.isNotEmpty(),
                    modifier = Modifier
                        .weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        modifier = Modifier.padding(vertical = 4.dp),
                        text = "Назад"
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        pathHistory.clear()
                        undonePaths.clear()
                        currentPath.value = Path()
                    },
                    enabled = pathHistory.isNotEmpty(),
                    modifier = Modifier
                        .weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        modifier = Modifier.padding(vertical = 4.dp),
                        text = "Стереть"
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                shape = RoundedCornerShape(8.dp),
                onClick = {
                    val widthPx = with(density) { 300.dp.toPx() }.toInt()
                    val heightPx = with(density) { 300.dp.toPx() }.toInt()

                    val bitmap = Bitmap.createBitmap(
                        widthPx,
                        heightPx,
                        Bitmap.Config.ARGB_8888
                    )
                    val canvas = android.graphics.Canvas(bitmap)
                    canvas.drawColor(Color.White.value.toInt())
                    val paint = android.graphics.Paint().apply {
                        color = Color.Black.toArgb()
                        strokeWidth = with(density) { 2.dp.toPx() }
                        strokeCap = android.graphics.Paint.Cap.ROUND
                        strokeJoin = android.graphics.Paint.Join.ROUND
                        style = android.graphics.Paint.Style.STROKE
                    }

                    pathHistory.forEach { path ->
                        canvas.drawPath(path.asAndroidPath(), paint)
                    }
                    if (!currentPath.value.isEmpty) {
                        canvas.drawPath(currentPath.value.asAndroidPath(), paint)
                    }

                    signatureBitmap = bitmap.asImageBitmap()
                    signatureBitmap?.let {
                        viewModel.saveSignature(it.toBase64())
                    }
                    onBackClickListener()
                },
                enabled = pathHistory.isNotEmpty() || !currentPath.value.isEmpty
            ) {
                Text(
                    modifier = Modifier.padding(vertical = 4.dp),
                    text = "Подтвердить"
                )
            }
        }
    }
}


fun isPathEmpty(path: Path): Boolean {
    val bounds = path.getBounds()
    return bounds.width == 0f && bounds.height == 0f
}

fun Path.rewindPath() {
    this.reset()
}

