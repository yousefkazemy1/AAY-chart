package com.aay.compose.wormChart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aay.compose.wormChart.components.wormCircle
import com.aay.compose.wormChart.components.wormLine
import kotlin.math.roundToInt

@Composable
fun WormChart(
    data: List<Double>,
    modifier: Modifier = Modifier,
    color: Color = Color.Red
) {
    val upperValue = remember { (data.maxOfOrNull { it }?.plus(1))?.roundToInt() ?: 0 }
    val lowerValue = remember { (data.minOfOrNull { it }?.toInt() ?: 0) }

    Box (modifier = modifier.fillMaxSize().padding(16.dp)) {
        Canvas(
            modifier = Modifier.aspectRatio(3/2f).align(Alignment.Center)
        ) {
            val chartWidth = size.width - 100.dp.toPx()
            val spacePerDataPoint = chartWidth / (data.size - 1)
            val centerY = size.height / 2
            val lastX = size.width - (100.dp.toPx() + (data.size - 1) * spacePerDataPoint)
            val lastY =
                centerY - ((data.last() - lowerValue) / (upperValue - lowerValue) * centerY).toFloat()

            wormLine(data, centerY, lowerValue, upperValue, lastX, lastY,color)
            wormCircle(lastX, lastY,color)
        }
    }
}
