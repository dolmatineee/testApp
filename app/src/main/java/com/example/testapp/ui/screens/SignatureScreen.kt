package com.example.testapp.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.testapp.ui.viewmodels.SignatureScreenViewModel
import com.example.testapp.utils.toBase64


@Composable
fun SignatureScreen(
    onBackClickListener: () -> Unit,
    viewModel: SignatureScreenViewModel = hiltViewModel()
) {
    var signatureBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    val tempPath = Path()
    val path = remember { mutableStateOf(Path()) }
    val density = LocalDensity.current // Получаем текущую плотность экрана

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
                    detectDragGestures { change, dragAmount ->
                        tempPath.moveTo(
                            change.position.x - dragAmount.x,
                            change.position.y - dragAmount.y
                        )
                        tempPath.lineTo(
                            change.position.x,
                            change.position.y
                        )
                        path.value = Path().apply {
                            addPath(tempPath)
                        }
                    }
                }
        ) {
            drawPath(
                path = path.value,
                color = Color.Black,
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Button(
            onClick = {
                // Преобразуем dp в пиксели
                val widthPx = with(density) { 300.dp.toPx() }.toInt()
                val heightPx = with(density) { 300.dp.toPx() }.toInt()

                // Создаем Bitmap из Canvas
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
                canvas.drawPath(path.value.asAndroidPath(), paint)

                // Преобразуем Bitmap в ImageBitmap
                signatureBitmap = bitmap.asImageBitmap()

                // Сохраняем изображение
                signatureBitmap?.let {
                    val base64Signature = it.toBase64()
                    viewModel.saveSignature(base64Signature)
                }
                onBackClickListener()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Подтвердить")
        }
    }
}