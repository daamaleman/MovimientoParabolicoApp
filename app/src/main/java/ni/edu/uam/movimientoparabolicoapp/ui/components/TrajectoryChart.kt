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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ni.edu.uam.movimientoparabolicoapp.domain.CollisionInfo
import ni.edu.uam.movimientoparabolicoapp.domain.Vector2D
import ni.edu.uam.movimientoparabolicoapp.ui.theme.CollisionGreen
import ni.edu.uam.movimientoparabolicoapp.ui.theme.ProjectileBlue
import ni.edu.uam.movimientoparabolicoapp.ui.theme.TargetOrange

/**
 * Componente que renderiza la gráfica analítica de altura (y) vs distancia (x).
 * 
 * A diferencia del Canvas de simulación, esta gráfica no representa el tiempo 
 * de forma directa, sino la relación espacial de las trayectorias.
 */
@Composable
fun TrajectoryChart(
    projectileTrajectory: List<Pair<Double, Vector2D>>,
    targetTrajectory: List<Pair<Double, Vector2D>>,
    projectilePos: Vector2D,
    targetPos: Vector2D,
    collisionInfo: CollisionInfo?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Gráfica de Trayectorias (Dinámica)",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .background(color = MaterialTheme.colorScheme.surfaceContainer, shape = RoundedCornerShape(24.dp))
                .padding(12.dp)
        ) {
            ChartCanvas(
                projectileTrajectory = projectileTrajectory,
                targetTrajectory = targetTrajectory,
                projectilePos = projectilePos,
                targetPos = targetPos,
                collisionInfo = collisionInfo
            )
        }

        // Leyenda de la gráfica
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LegendItem(color = ProjectileBlue, label = "Proyectil")
            LegendItem(color = TargetOrange, label = "Objetivo")
            LegendItem(color = CollisionGreen, label = "Impacto")
        }
    }
}

/**
 * Lienzo de dibujo para la gráfica analítica.
 */
@Composable
private fun ChartCanvas(
    projectileTrajectory: List<Pair<Double, Vector2D>>,
    targetTrajectory: List<Pair<Double, Vector2D>>,
    projectilePos: Vector2D,
    targetPos: Vector2D,
    collisionInfo: CollisionInfo?
) {
    val onSurface = MaterialTheme.colorScheme.onSurface
    
    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxWidth()) {
        val width = size.width
        val height = size.height

        // Obtenemos todos los puntos para calcular el encuadre automático
        val allX = (projectileTrajectory + targetTrajectory).map { it.second.x }
        val allY = (projectileTrajectory + targetTrajectory).map { it.second.y }
        if (allX.isEmpty()) return@Canvas

        val rawMaxX = allX.maxOrNull() ?: 10.0
        val rawMaxY = allY.maxOrNull() ?: 10.0
        
        // Añadimos márgenes para que las etiquetas no se corten
        val maxX = rawMaxX * 1.15
        val maxY = rawMaxY * 1.4

        /**
         * Función interna para transformar coordenadas del mundo real a coordenadas de dibujo.
         */
        fun worldToCanvas(x: Double, y: Double): androidx.compose.ui.geometry.Offset {
            val hPadding = 40f
            val vPadding = 40f
            val availableWidth = width - (hPadding * 2)
            val availableHeight = height - (vPadding * 2)

            val cx = hPadding + (x / maxX * availableWidth).toFloat()
            val cy = (height - vPadding) - (y / maxY * availableHeight).toFloat()
            return androidx.compose.ui.geometry.Offset(cx, cy)
        }

        // 1. Dibujamos la Grilla y Ejes de referencia
        val gridColor = onSurface.copy(alpha = 0.05f)
        val axisColor = onSurface.copy(alpha = 0.4f)
        val origin = worldToCanvas(0.0, 0.0)
        for (i in 1..4) {
            val yPos = worldToCanvas(0.0, (maxY / 5) * i).y
            drawLine(color = gridColor, start = androidx.compose.ui.geometry.Offset(0f, yPos), end = androidx.compose.ui.geometry.Offset(width, yPos))
        }
        drawLine(color = axisColor, start = androidx.compose.ui.geometry.Offset(origin.x, 0f), end = androidx.compose.ui.geometry.Offset(origin.x, height), strokeWidth = 2f)
        drawLine(color = axisColor, start = androidx.compose.ui.geometry.Offset(0f, origin.y), end = androidx.compose.ui.geometry.Offset(width, origin.y), strokeWidth = 2f)

        // 2. Dibujamos las Áreas Sombreadas bajo las parábolas
        drawAreaUnderTrajectory(projectileTrajectory, ProjectileBlue.copy(alpha = 0.1f)) { x, y -> worldToCanvas(x, y) }
        drawAreaUnderTrajectory(targetTrajectory, TargetOrange.copy(alpha = 0.1f)) { x, y -> worldToCanvas(x, y) }

        // 3. Dibujamos las Líneas de Trayectoria
        drawPathFromTrajectory(projectileTrajectory, ProjectileBlue, 4f) { x, y -> worldToCanvas(x, y) }
        drawPathFromTrajectory(targetTrajectory, TargetOrange, 4f) { x, y -> worldToCanvas(x, y) }

        // 4. Dibujamos los Indicadores de Posición Actual (Puntos móviles)
        val currentProj = worldToCanvas(projectilePos.x, projectilePos.y)
        val currentTarget = worldToCanvas(targetPos.x, targetPos.y)
        drawCircle(color = ProjectileBlue.copy(alpha = 0.3f), radius = 12f, center = currentProj)
        drawCircle(color = ProjectileBlue, radius = 6f, center = currentProj)
        drawCircle(color = TargetOrange.copy(alpha = 0.3f), radius = 12f, center = currentTarget)
        drawCircle(color = TargetOrange, radius = 6f, center = currentTarget)

        // 5. Dibujamos las Etiquetas de Escala (Native Canvas para texto avanzado)
        drawContext.canvas.nativeCanvas.apply {
            val paint = Paint().apply { color = android.graphics.Color.WHITE; alpha = 120; textSize = 24f; isAntiAlias = true }
            drawText("Y (m)", origin.x + 10f, 30f, paint)
            drawText("X (m)", width - 80f, origin.y - 10f, paint)
            paint.textAlign = Paint.Align.RIGHT
            drawText("${String.format("%.1f", rawMaxY)}", origin.x - 10f, worldToCanvas(0.0, rawMaxY).y + 10f, paint)
            paint.textAlign = Paint.Align.CENTER
            drawText("${String.format("%.1f", rawMaxX)}", worldToCanvas(rawMaxX, 0.0).x, origin.y + 30f, paint)
        }

        // 6. Si hay colisión, marcamos el punto de impacto
        if (collisionInfo != null && collisionInfo.occurred) {
            val pos = worldToCanvas(collisionInfo.position.x, collisionInfo.position.y)
            drawCircle(color = CollisionGreen, radius = 10f, center = pos)
            drawCircle(color = CollisionGreen.copy(alpha = 0.2f), radius = 25f, center = pos)
        }
    }
}

/**
 * Función utilitaria para dibujar una línea de trayectoria suavizada.
 */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawPathFromTrajectory(
    trajectory: List<Pair<Double, Vector2D>>,
    color: Color,
    strokeWidth: Float,
    transform: (Double, Double) -> androidx.compose.ui.geometry.Offset
) {
    val points = trajectory.filter { it.second.y >= -0.5 }.map { transform(it.second.x, it.second.y) }
    if (points.size < 2) return
    val path = Path().apply {
        moveTo(points[0].x, points[0].y)
        points.drop(1).forEach { lineTo(it.x, it.y) }
    }
    drawPath(path = path, color = color, style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth, cap = androidx.compose.ui.graphics.StrokeCap.Round, join = androidx.compose.ui.graphics.StrokeJoin.Round))
}

/**
 * Rellena el área bajo la curva para mejorar la percepción visual de la trayectoria.
 */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawAreaUnderTrajectory(
    trajectory: List<Pair<Double, Vector2D>>,
    color: Color,
    transform: (Double, Double) -> androidx.compose.ui.geometry.Offset
) {
    val points = trajectory.filter { it.second.y >= 0 }.map { transform(it.second.x, it.second.y) }
    if (points.size < 2) return
    val originY = transform(0.0, 0.0).y
    val path = Path().apply {
        moveTo(points[0].x, originY)
        points.forEach { lineTo(it.x, it.y) }
        lineTo(points.last().x, originY)
        close()
    }
    drawPath(path = path, color = color)
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(modifier = Modifier.size(10.dp).background(color = color, shape = RoundedCornerShape(3.dp)))
        Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
    }
}
