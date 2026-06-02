package ni.edu.uam.movimientoparabolicoapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
 * Gráfica de trayectorias y(x).
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
            text = "Trayectorias y(x)",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
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
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LegendItem(color = ProjectileBlue, label = "Proyectil")
            LegendItem(color = TargetOrange, label = "Objetivo")
            LegendItem(color = CollisionGreen, label = "Colisión")
        }

        Text(
            text = "Gráfica analítica de altura (y) vs distancia horizontal (x).",
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
    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxWidth()) {
        val width = size.width
        val height = size.height

        val allX = (projectileTrajectory + targetTrajectory).map { it.second.x }
        val allY = (projectileTrajectory + targetTrajectory).map { it.second.y }

        if (allX.isEmpty() || allY.isEmpty()) return@Canvas

        val maxX = (allX.maxOrNull() ?: 10.0) * 1.1
        val maxY = (allY.maxOrNull() ?: 10.0) * 1.1

        fun worldToCanvas(x: Double, y: Double): androidx.compose.ui.geometry.Offset {
            val cx = (x / maxX * width).toFloat()
            val cy = (height - (y / maxY * height)).toFloat()
            return androidx.compose.ui.geometry.Offset(cx, cy)
        }

        // Guías
        val guideColor = Color.LightGray.copy(alpha = 0.2f)
        for (i in 1..4) {
            val y = height * i / 5
            drawLine(color = guideColor, start = androidx.compose.ui.geometry.Offset(0f, y), end = androidx.compose.ui.geometry.Offset(width, y), strokeWidth = 1f)
        }

        // Proyectil
        drawPathFromTrajectory(projectileTrajectory, ProjectileBlue) { x, y -> worldToCanvas(x, y) }

        // Objetivo
        drawPathFromTrajectory(targetTrajectory, TargetOrange) { x, y -> worldToCanvas(x, y) }

        // Punto de colisión
        if (collisionInfo != null && collisionInfo.occurred) {
            val pos = worldToCanvas(collisionInfo.position.x, collisionInfo.position.y)
            drawCircle(color = CollisionGreen, radius = 6f, center = pos)
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawPathFromTrajectory(
    trajectory: List<Pair<Double, Vector2D>>,
    color: Color,
    transform: (Double, Double) -> androidx.compose.ui.geometry.Offset
) {
    val points = trajectory.filter { it.second.y >= 0 }.map { transform(it.second.x, it.second.y) }
    if (points.size < 2) return

    val path = androidx.compose.ui.graphics.Path().apply {
        moveTo(points[0].x, points[0].y)
        points.drop(1).forEach { lineTo(it.x, it.y) }
    }

    drawPath(
        path = path,
        color = color,
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f)
    )
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color = color, shape = RoundedCornerShape(2.dp))
        )
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
