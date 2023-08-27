package com.aay.compose.lineChart.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.aay.compose.lineChart.model.LineParameters
import com.aay.compose.utils.clickedOnThisPoint
import com.aay.compose.utils.formatToThousandsMillionsBillions
import kotlin.math.roundToInt

private var lastClickedPoint: Pair<Float, Float>? = null

@OptIn(ExperimentalTextApi::class)
fun DrawScope.drawDefaultLineWithShadow(
    line: LineParameters,
    lowerValue: Float,
    upperValue: Float,
    animatedProgress: Animatable<Float, AnimationVector1D>,
    xAxisSize: Int,
    spacingX: Dp,
    spacingY: Dp,
    clickedPoints: MutableList<Pair<Float, Float>>,
    textMeasure: TextMeasurer,
    xAxisData: List<String>,
) {

    val textLayoutResult = textMeasure.measure(
        text = AnnotatedString(xAxisData.first().toString()),
    ).size.width

    val startSpace = (spacingX ) + (textLayoutResult / 2).dp
    val spaceBetweenXes = ((size.width - startSpace.toPx()) / (xAxisData.size - 1)).toDp()

    val strokePathOfDefaultLine = drawLineAsDefault(
        lineParameter = line,
        lowerValue = lowerValue,
        upperValue = upperValue,
        animatedProgress = animatedProgress,
        spacingX = spacingX,
        spacingY = spacingY,
        clickedPoints = clickedPoints,
        textMeasure = textMeasure,
        xAxisData = xAxisData
    )

    if (line.lineShadow) {
        val fillPath = strokePathOfDefaultLine.apply {
            lineTo(size.width - spaceBetweenXes.toPx() + 40.dp.toPx(), size.height * 40)
            lineTo(spacingX.toPx() * 2, size.height * 40)
            close()
        }
        clipRect(right = size.width * animatedProgress.value) {
            drawPath(
                path = fillPath, brush = Brush.verticalGradient(
                    colors = listOf(line.lineColor.copy(alpha = .3f), Color.Transparent),
                    endY = (size.height.toDp() - spacingY).toPx()
                )
            )
        }
    }
}

@OptIn(ExperimentalTextApi::class)
private fun DrawScope.drawLineAsDefault(
    lineParameter: LineParameters,
    lowerValue: Float,
    upperValue: Float,
    animatedProgress: Animatable<Float, AnimationVector1D>,
    spacingX: Dp,
    spacingY: Dp,
    clickedPoints: MutableList<Pair<Float, Float>>,
    textMeasure: TextMeasurer,
    xAxisData: List<String>
) = Path().apply {
    val height = size.height.toDp()
    drawPathLineWrapper(
        lineParameter = lineParameter,
        strokePath = this,
        animatedProgress = animatedProgress,
    ) { lineParameter, index ->
        val textLayoutResult = textMeasure.measure(
            text = AnnotatedString(xAxisData[index]),
        ).size.width

        val startSpace = (spacingX ) + (textLayoutResult / 2).dp
        val spaceBetweenXes = ((size.width - startSpace.toPx()) / (xAxisData.size - 1)).toDp()
        val yTextLayoutResult = textMeasure.measure(
            text = AnnotatedString(upperValue.formatToThousandsMillionsBillions()),
        ).size.width

        val info = lineParameter.data[index]
        val ratio = (info - lowerValue) / (upperValue - lowerValue)
        val startXPoint = (yTextLayoutResult.dp) + (index * spaceBetweenXes)
        val startYPoint =
            (height.toPx() + 14.dp.toPx() - spacingY.toPx() - (ratio * (height.toPx() - spacingY.toPx())))

        val tolerance = 20.dp.toPx()
        val savedClicks =
            clickedOnThisPoint(clickedPoints, startXPoint.toPx(), startYPoint, tolerance)


        if (savedClicks) {
            if (lastClickedPoint != null) {
                clickedPoints.clear()
                lastClickedPoint = null
            } else {
                lastClickedPoint = Pair(startXPoint.toPx(), startYPoint.toFloat())
                circleWithRectAndText(
                    x = startXPoint,
                    y = startYPoint,
                    textMeasure = textMeasure,
                    info = info,
                    stroke = Stroke(width = 2.dp.toPx()),
                    line = lineParameter,
                    animatedProgress = animatedProgress
                )
            }
        }

        if (index == 0) {
            moveTo(startXPoint.toPx(), startYPoint.toFloat())
        } else {
            lineTo(startXPoint.toPx(), startYPoint.toFloat())
        }
    }
}

