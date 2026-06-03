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

        // Calculamos los límites máximos pero aseguramos que el mínimo sea 0
        // y añadimos un margen del 20% para que no se pegue a los bordes
        val maxX = (allX.maxOrNull() ?: 10.0).coerceAtLeast(1.0) * 1.2
        val maxY = (allY.maxOrNull() ?: 10.0).coerceAtLeast(1.0) * 1.2

        // Usamos el máximo entre X e Y para mantener una relación de aspecto 1:1 
        // y que la parábola no se vea deformada/estirada, o simplemente escalamos según el canvas.
        // Para que se vea "bien" y no tan arriba, vamos a usar escalas independientes pero con padding.

        fun worldToCanvas(x: Double, y: Double): androidx.compose.ui.geometry.Offset {
            // Dejamos un pequeño margen interno en el dibujo (padding de 10px)
            val padding = 20f
            val availableWidth = width - (padding * 2)
            val availableHeight = height - (padding * 2)

            val cx = padding + (x / maxX * availableWidth).toFloat()
            // Invertimos Y para que 0 esté abajo, y aplicamos el padding
            val cy = (height - padding) - (y / maxY * availableHeight).toFloat()
            return androidx.compose.ui.geometry.Offset(cx, cy)
        }

        // Guías de fondo (Grilla)
        val guideColor = Color.LightGray.copy(alpha = 0.2f)
        val divisions = 5
        for (i in 0..divisions) {
            // Líneas horizontales
            val yPos = worldToCanvas(0.0, (maxY / divisions) * i).y
            drawLine(color = guideColor, start = androidx.compose.ui.geometry.Offset(0f, yPos), end = androidx.compose.ui.geometry.Offset(width, yPos), strokeWidth = 1f)
            
            // Líneas verticales
            val xPos = worldToCanvas((maxX / divisions) * i, 0.0).x
            drawLine(color = guideColor, start = androidx.compose.ui.geometry.Offset(xPos, 0f), end = androidx.compose.ui.geometry.Offset(xPos, height), strokeWidth = 1f)
        }

        // Ejes X e Y resaltados
        val axisColor = Color.Gray.copy(alpha = 0.5f)
        val origin = worldToCanvas(0.0, 0.0)
        drawLine(color = axisColor, start = androidx.compose.ui.geometry.Offset(0f, origin.y), end = androidx.compose.ui.geometry.Offset(width, origin.y), strokeWidth = 2f)
        drawLine(color = axisColor, start = androidx.compose.ui.geometry.Offset(origin.x, 0f), end = androidx.compose.ui.geometry.Offset(origin.x, height), strokeWidth = 2f)

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
