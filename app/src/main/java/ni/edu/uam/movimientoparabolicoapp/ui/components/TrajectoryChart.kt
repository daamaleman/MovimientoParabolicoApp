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

/**
 * Gráfica de trayectorias usando Canvas.
 *
 * Muestra y(x): altura vs posición horizontal de ambos objetos,
 * incluyendo el punto de colisión.
 */
@Composable
fun TrajectoryChart(
    projectileTrajectory: List<Pair<Double, Vector2D>>,
    targetTrajectory: List<Pair<Double, Vector2D>>,
    collisionInfo: CollisionInfo?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    ) {
        // Título
        Text(
            text = "Trayectorias y(x)",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Gráfica
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(12.dp)
        ) {
            ChartPlaceholder(
                projectileTrajectory = projectileTrajectory,
                targetTrajectory = targetTrajectory,
                collisionInfo = collisionInfo
            )
        }

        // Leyenda
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LegendItem(color = Color(0xFF4f5bd5), label = "Proyectil A")
            Spacer(modifier = Modifier.padding(horizontal = 8.dp))
            LegendItem(color = Color(0xFFe0552b), label = "Objetivo B")
            Spacer(modifier = Modifier.padding(horizontal = 8.dp))
            LegendItem(color = Color(0xFF1f8a4c), label = "Punto de choque")
        }

        // Nota de información
        Text(
            text = "Curvas generadas a partir de las ecuaciones de cinemática. " +
                    "Parabólico perfecto sin restricciones de aire.",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ChartPlaceholder(
    projectileTrajectory: List<Pair<Double, Vector2D>>,
    targetTrajectory: List<Pair<Double, Vector2D>>,
    collisionInfo: CollisionInfo?
) {
    // Renderiza un gráfico de canvas simple para visualizar las trayectorias y(x)
    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxWidth()) {
        val width = size.width
        val height = size.height

        // Obtiene los límites de los datos
        val allX = (projectileTrajectory + targetTrajectory).map { it.second.x }
        val allY = (projectileTrajectory + targetTrajectory).map { it.second.y }

        if (allX.isEmpty() || allY.isEmpty()) return@Canvas

        val maxX = allX.maxOrNull() ?: 10.0
        val maxY = allY.maxOrNull() ?: 10.0

        // Márgenes
        val marginL = 30f
        val marginB = 20f

        // Función de transformación de mundo a canvas
        fun worldToCanvas(x: Double, y: Double): Pair<Float, Float> {
            val canvasX = (marginL + (x / maxX) * (width - marginL - 10f)).toFloat()
            val canvasY = (height - marginB - (y / maxY) * (height - marginB - 10f)).toFloat()
            return Pair(canvasX, canvasY)
        }

        // Dibuja ejes
        drawLine(
            color = Color.Black.copy(alpha = 0.3f),
            start = androidx.compose.ui.geometry.Offset(marginL, height - marginB),
            end = androidx.compose.ui.geometry.Offset(width, height - marginB),
            strokeWidth = 1f
        )
        drawLine(
            color = Color.Black.copy(alpha = 0.3f),
            start = androidx.compose.ui.geometry.Offset(marginL, height - marginB),
            end = androidx.compose.ui.geometry.Offset(marginL, 10f),
            strokeWidth = 1f
        )

        // Dibuja trayectoria del proyectil
        val projPoints = projectileTrajectory
            .filter { it.second.y >= 0 }
            .map { worldToCanvas(it.second.x, it.second.y) }

        val projPath = androidx.compose.ui.graphics.Path()
        if (projPoints.isNotEmpty()) {
            projPath.moveTo(projPoints[0].first, projPoints[0].second)
            projPoints.drop(1).forEach { (x, y) ->
                projPath.lineTo(x, y)
            }

            drawPath(
                path = projPath,
                color = Color(0xFF4f5bd5),
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = 2.5f
                )
            )
        }

        // Dibuja trayectoria del objetivo
        val targetPoints = targetTrajectory
            .filter { it.second.y >= 0 }
            .map { worldToCanvas(it.second.x, it.second.y) }

        val targetPath = androidx.compose.ui.graphics.Path()
        if (targetPoints.isNotEmpty()) {
            targetPath.moveTo(targetPoints[0].first, targetPoints[0].second)
            targetPoints.drop(1).forEach { (x, y) ->
                targetPath.lineTo(x, y)
            }

            drawPath(
                path = targetPath,
                color = Color(0xFFe0552b),
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = 2.5f
                )
            )
        }

        // Dibuja punto de colisión
        if (collisionInfo != null && collisionInfo.occurred) {
            val (cx, cy) = worldToCanvas(collisionInfo.position.x, collisionInfo.position.y)
            drawCircle(
                color = Color(0xFF1f8a4c),
                radius = 5f,
                center = androidx.compose.ui.geometry.Offset(cx, cy)
            )
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color = color, shape = RoundedCornerShape(50.dp))
        )
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}





