package ni.edu.uam.movimientoparabolicoapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ni.edu.uam.movimientoparabolicoapp.domain.CollisionInfo
import ni.edu.uam.movimientoparabolicoapp.domain.Vector2D
import ni.edu.uam.movimientoparabolicoapp.ui.theme.CanvasBg
import ni.edu.uam.movimientoparabolicoapp.ui.theme.CollisionGreen
import ni.edu.uam.movimientoparabolicoapp.ui.theme.GroundGreen
import ni.edu.uam.movimientoparabolicoapp.ui.theme.ProjectileBlue
import ni.edu.uam.movimientoparabolicoapp.ui.theme.TargetOrange

/**
 * Componente visual que renderiza la simulación física en tiempo real.
 * 
 * Utiliza el API de Canvas de Jetpack Compose para dibujar vectores físicos 
 * en coordenadas de pantalla, realizando la conversión de metros a píxeles.
 */
@Composable
fun SimulationCanvas(
    modifier: Modifier = Modifier,
    currentTime: Double,
    projectilePos: Vector2D,
    targetPos: Vector2D,
    projectileTrajectory: List<Pair<Double, Vector2D>>,
    targetTrajectory: List<Pair<Double, Vector2D>>,
    collisionInfo: CollisionInfo?,
    maxSimulationTime: Double,
    initialProjectilePos: Vector2D,
    launchAngleRadians: Double
) {
    val surfaceContainerColor = MaterialTheme.colorScheme.surfaceContainer
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(color = CanvasBg)
    ) {
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // 1. Calculamos los límites dinámicos para que el zoom se ajuste a los objetos
            val worldBounds = calculateWorldBounds(
                projectilePos = projectilePos,
                targetPos = targetPos,
                projTraj = projectileTrajectory,
                targetTraj = targetTrajectory,
                collision = collisionInfo
            )

            // 2. Fondo y Decoración (Suelo)
            val origin = world2Canvas(Vector2D(0.0, 0.0), worldBounds, canvasWidth, canvasHeight)
            val groundY = origin.y
            drawRect(color = surfaceContainerColor.copy(alpha = 0.1f), size = size)
            drawRect(color = GroundGreen, topLeft = Offset(0f, groundY), size = androidx.compose.ui.geometry.Size(canvasWidth, canvasHeight - groundY))
            drawLine(color = Color(0xFF88A085), start = Offset(0f, groundY), end = Offset(canvasWidth, groundY), strokeWidth = 2f)

            // 3. Cuadrícula de Referencia
            drawGrid(worldBounds, canvasWidth, canvasHeight)

            // 4. LÍNEA DE APUNTADO (Guía visual del disparo inicial)
            val startPointAim = world2Canvas(initialProjectilePos, worldBounds, canvasWidth, canvasHeight)
            val aimLength = 1000.0 
            val endPointWorld = Vector2D(
                initialProjectilePos.x + aimLength * kotlin.math.cos(launchAngleRadians),
                initialProjectilePos.y + aimLength * kotlin.math.sin(launchAngleRadians)
            )
            val endPointAim = world2Canvas(endPointWorld, worldBounds, canvasWidth, canvasHeight)
            drawLine(
                color = ProjectileBlue.copy(alpha = 0.2f),
                start = startPointAim,
                end = endPointAim,
                strokeWidth = 1.5f,
                pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
            )

            // 5. Trayectorias Predictivas (Líneas punteadas)
            drawTrajectory(projectileTrajectory, worldBounds, canvasWidth, canvasHeight, ProjectileBlue, 1.2f, true)
            drawTrajectory(targetTrajectory, worldBounds, canvasWidth, canvasHeight, TargetOrange, 1.2f, true)

            // 6. Trayectorias Recorridas (Líneas sólidas)
            val traveledProj = projectileTrajectory.filter { it.first <= currentTime }
            drawTrajectory(traveledProj, worldBounds, canvasWidth, canvasHeight, ProjectileBlue, 2f, false)

            // 7. Renderizado de Objetos (Bolas)
            drawObject(projectilePos, worldBounds, canvasWidth, canvasHeight, ProjectileBlue, 10f)
            drawObject(targetPos, worldBounds, canvasWidth, canvasHeight, TargetOrange, 10f)

            // 8. Marcador de Colisión (Si ocurrió impacto)
            if (collisionInfo != null && collisionInfo.occurred) {
                drawCollisionPoint(collisionInfo.position, worldBounds, canvasWidth, canvasHeight)
            }
        }

        // Overlay de tiempo digital
        Box(
            modifier = Modifier
                .padding(12.dp)
                .background(color = Color.Black.copy(alpha = 0.6f), shape = RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(text = "t = ${String.format("%.2f", currentTime)} s", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        }
    }
}

/**
 * Función crucial que convierte metros (mundo real) a píxeles (pantalla).
 * 
 * Implementa una transformación lineal considerando el padding y la inversión del eje Y
 * (ya que en pantalla el 0,0 está arriba a la izquierda).
 */
private fun DrawScope.world2Canvas(
    worldPos: Vector2D,
    worldBounds: WorldBounds,
    canvasWidth: Float,
    canvasHeight: Float
): Offset {
    val paddingSide = 60f
    val paddingBottom = 60f
    val paddingTop = 40f
    
    val availableWidth = canvasWidth - (paddingSide * 2)
    val availableHeight = canvasHeight - paddingBottom - paddingTop
    
    val canvasX = paddingSide + ((worldPos.x / worldBounds.maxX) * availableWidth).toFloat()
    // Inversión de eje Y: (Altura Total - Desplazamiento)
    val canvasY = (canvasHeight - paddingBottom) - ((worldPos.y / worldBounds.maxY) * availableHeight).toFloat()
    
    return Offset(canvasX, canvasY)
}

/**
 * Dibuja un círculo estilizado que representa un proyectil u objetivo.
 */
private fun DrawScope.drawObject(
    pos: Vector2D,
    worldBounds: WorldBounds,
    canvasWidth: Float,
    canvasHeight: Float,
    color: Color,
    radius: Float
) {
    val canvasPos = world2Canvas(pos, worldBounds, canvasWidth, canvasHeight)
    drawCircle(color = color, radius = radius, center = canvasPos)
    // Efecto de brillo/reflejo para darle volumen
    drawCircle(color = Color.White, radius = radius * 0.4f, center = Offset(canvasPos.x - radius*0.3f, canvasPos.y - radius*0.3f), alpha = 0.4f)
}

/**
 * Calcula los límites máximos de visualización para que todos los objetos
 * importantes queden siempre dentro de la pantalla.
 */
private fun DrawScope.calculateWorldBounds(
    projectilePos: Vector2D,
    targetPos: Vector2D,
    projTraj: List<Pair<Double, Vector2D>>,
    targetTraj: List<Pair<Double, Vector2D>>,
    collision: CollisionInfo?
): WorldBounds {
    val allPositions = mutableListOf<Vector2D>()
    allPositions.add(projectilePos)
    allPositions.add(targetPos)
    allPositions.addAll(projTraj.map { it.second })
    allPositions.addAll(targetTraj.map { it.second })
    if (collision != null) allPositions.add(collision.position)

    val maxX = (allPositions.maxOfOrNull { it.x } ?: 10.0) + 5.0
    val maxY = (allPositions.maxOfOrNull { it.y } ?: 8.0) + 5.0
    return WorldBounds(maxX = maxX, maxY = maxY)
}

private data class WorldBounds(val maxX: Double, val maxY: Double)

/**
 * Dibuja las líneas de trayectoria conectando puntos consecutivos.
 */
private fun DrawScope.drawTrajectory(
    trajectory: List<Pair<Double, Vector2D>>,
    worldBounds: WorldBounds,
    canvasWidth: Float,
    canvasHeight: Float,
    color: Color,
    strokeWidth: Float,
    isDashed: Boolean
) {
    if (trajectory.isEmpty()) return
    val points = trajectory.map { world2Canvas(it.second, worldBounds, canvasWidth, canvasHeight) }
    if (points.size < 2) return

    val path = androidx.compose.ui.graphics.Path().apply {
        moveTo(points[0].x, points[0].y)
        points.drop(1).forEach { lineTo(it.x, it.y) }
    }

    drawPath(
        path = path,
        color = color,
        style = Stroke(
            width = strokeWidth,
            pathEffect = if (isDashed) androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(8f, 8f)) else null
        ),
        alpha = if (isDashed) 0.4f else 1.0f
    )
}

/**
 * Renderiza la cuadrícula de fondo.
 */
private fun DrawScope.drawGrid(worldBounds: WorldBounds, canvasWidth: Float, canvasHeight: Float) {
    val gridCount = 8
    val color = Color.Gray.copy(alpha = 0.15f)
    val origin = world2Canvas(Vector2D(0.0, 0.0), worldBounds, canvasWidth, canvasHeight)
    for (i in 0..gridCount) {
        val xVal = (worldBounds.maxX / gridCount) * i
        val xPos = world2Canvas(Vector2D(xVal, 0.0), worldBounds, canvasWidth, canvasHeight).x
        drawLine(color = color, start = Offset(xPos, 20f), end = Offset(xPos, origin.y), strokeWidth = 1f)
        val yVal = (worldBounds.maxY / gridCount) * i
        val yPos = world2Canvas(Vector2D(0.0, yVal), worldBounds, canvasWidth, canvasHeight).y
        if (yPos > 20f) drawLine(color = color, start = Offset(origin.x, yPos), end = Offset(canvasWidth - 20f, yPos), strokeWidth = 1f)
    }
}

/**
 * Dibuja el marcador visual de impacto (punto verde con halo).
 */
private fun DrawScope.drawCollisionPoint(pos: Vector2D, worldBounds: WorldBounds, canvasWidth: Float, canvasHeight: Float) {
    val canvasPos = world2Canvas(pos, worldBounds, canvasWidth, canvasHeight)
    drawCircle(color = CollisionGreen, radius = 14f, center = canvasPos, alpha = 0.3f)
    drawCircle(color = CollisionGreen, radius = 6f, center = canvasPos)
}
