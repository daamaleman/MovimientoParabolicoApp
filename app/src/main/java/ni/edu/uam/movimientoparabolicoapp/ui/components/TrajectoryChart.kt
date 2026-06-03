package ni.edu.uam.movimientoparabolicoapp.ui.components

import android.graphics.Paint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ni.edu.uam.movimientoparabolicoapp.domain.CollisionInfo
import ni.edu.uam.movimientoparabolicoapp.domain.Vector2D
import ni.edu.uam.movimientoparabolicoapp.ui.theme.CollisionGreen
import ni.edu.uam.movimientoparabolicoapp.ui.theme.ProjectileBlue
import ni.edu.uam.movimientoparabolicoapp.ui.theme.TargetOrange

/**
 * Gráfica de trayectorias y(x) mejorada visualmente.
 */
@Composable
fun TrajectoryChart(
    projectileTrajectory: List<Pair<Double, Vector2D>>,
    targetTrajectory: List<Pair<Double, Vector2D>>,
    collisionInfo: CollisionInfo?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "Análisis de Trayectorias y(x)",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(16.dp)
        ) {
            ChartCanvas(
                projectileTrajectory = projectileTrajectory,
                targetTrajectory = targetTrajectory,
                collisionInfo = collisionInfo
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LegendItem(color = ProjectileBlue, label = "Proyectil")
            LegendItem(color = TargetOrange, label = "Objetivo")
            LegendItem(color = CollisionGreen, label = "Colisión")
        }

        Text(
            text = "Gráfica de altura (y) vs distancia (x) en metros.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ChartCanvas(
    projectileTrajectory: List<Pair<Double, Vector2D>>,
    targetTrajectory: List<Pair<Double, Vector2D>>,
    collisionInfo: CollisionInfo?
) {
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    
    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxWidth()) {
        val width = size.width
        val height = size.height

        val allX = (projectileTrajectory + targetTrajectory).map { it.second.x }
        val allY = (projectileTrajectory + targetTrajectory).map { it.second.y }

        if (allX.isEmpty() || allY.isEmpty()) return@Canvas

        val maxX = (allX.maxOrNull() ?: 10.0).coerceAtLeast(1.0) * 1.2
        val maxY = (allY.maxOrNull() ?: 10.0).coerceAtLeast(1.0) * 1.3 // Más margen arriba

        fun worldToCanvas(x: Double, y: Double): androidx.compose.ui.geometry.Offset {
            val padding = 24f
            val availableWidth = width - (padding * 2)
            val availableHeight = height - (padding * 2)

            val cx = padding + (x / maxX * availableWidth).toFloat()
            val cy = (height - padding) - (y / maxY * availableHeight).toFloat()
            return androidx.compose.ui.geometry.Offset(cx, cy)
        }

        // Grilla
        val guideColor = Color.Gray.copy(alpha = 0.1f)
        val divisions = 5
        for (i in 0..divisions) {
            val yVal = (maxY / divisions) * i
            val yPos = worldToCanvas(0.0, yVal).y
            drawLine(color = guideColor, start = androidx.compose.ui.geometry.Offset(0f, yPos), end = androidx.compose.ui.geometry.Offset(width, yPos), strokeWidth = 1f)
            
            val xVal = (maxX / divisions) * i
            val xPos = worldToCanvas(xVal, 0.0).x
            drawLine(color = guideColor, start = androidx.compose.ui.geometry.Offset(xPos, 0f), end = androidx.compose.ui.geometry.Offset(xPos, height), strokeWidth = 1f)
        }

        // Ejes
        val axisColor = onSurfaceColor.copy(alpha = 0.6f)
        val origin = worldToCanvas(0.0, 0.0)
        drawLine(color = axisColor, start = androidx.compose.ui.geometry.Offset(0f, origin.y), end = androidx.compose.ui.geometry.Offset(width, origin.y), strokeWidth = 2f)
        drawLine(color = axisColor, start = androidx.compose.ui.geometry.Offset(origin.x, 0f), end = androidx.compose.ui.geometry.Offset(origin.x, height), strokeWidth = 2f)

        // Etiquetas de escala
        drawContext.canvas.nativeCanvas.apply {
            val paint = Paint().apply {
                color = android.graphics.Color.GRAY
                textSize = 28f
                textAlign = Paint.Align.LEFT
            }
            drawText("${String.format("%.1f", maxY)}m", origin.x + 8f, 30f, paint)
            drawText("${String.format("%.1f", maxX)}m", width - 80f, origin.y - 8f, paint)
        }

        // Trayectorias con sombra/glow
        drawPathFromTrajectory(projectileTrajectory, ProjectileBlue.copy(alpha = 0.2f), 8f) { x, y -> worldToCanvas(x, y) }
        drawPathFromTrajectory(projectileTrajectory, ProjectileBlue, 4f) { x, y -> worldToCanvas(x, y) }

        drawPathFromTrajectory(targetTrajectory, TargetOrange.copy(alpha = 0.2f), 8f) { x, y -> worldToCanvas(x, y) }
        drawPathFromTrajectory(targetTrajectory, TargetOrange, 4f) { x, y -> worldToCanvas(x, y) }

        // Colisión
        if (collisionInfo != null && collisionInfo.occurred) {
            val pos = worldToCanvas(collisionInfo.position.x, collisionInfo.position.y)
            drawCircle(color = CollisionGreen, radius = 8f, center = pos)
            drawCircle(color = CollisionGreen.copy(alpha = 0.3f), radius = 16f, center = pos)
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawPathFromTrajectory(
    trajectory: List<Pair<Double, Vector2D>>,
    color: Color,
    strokeWidth: Float,
    transform: (Double, Double) -> androidx.compose.ui.geometry.Offset
) {
    val points = trajectory.filter { it.second.y >= -0.1 }.map { transform(it.second.x, it.second.y) }
    if (points.size < 2) return

    val path = androidx.compose.ui.graphics.Path().apply {
        moveTo(points[0].x, points[0].y)
        points.drop(1).forEach { lineTo(it.x, it.y) }
    }

    drawPath(
        path = path,
        color = color,
        style = androidx.compose.ui.graphics.drawscope.Stroke(
            width = strokeWidth,
            cap = androidx.compose.ui.graphics.StrokeCap.Round,
            join = androidx.compose.ui.graphics.StrokeJoin.Round
        )
    )
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color = color, shape = RoundedCornerShape(4.dp))
        )
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
