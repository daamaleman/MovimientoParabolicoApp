package ni.edu.uam.movimientoparabolicoapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import ni.edu.uam.movimientoparabolicoapp.ui.components.CollisionBanner
import ni.edu.uam.movimientoparabolicoapp.ui.components.ParameterSliders
import ni.edu.uam.movimientoparabolicoapp.ui.components.PositionReadout
import ni.edu.uam.movimientoparabolicoapp.ui.components.SimulationCanvas
import ni.edu.uam.movimientoparabolicoapp.ui.components.TrajectoryChart
import ni.edu.uam.movimientoparabolicoapp.ui.components.TransportControls
import ni.edu.uam.movimientoparabolicoapp.ui.theme.ProjectileBlue

/**
 * Pantalla principal de la simulación rediseñada para coincidir con el mockup.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimulationScreen(
    modifier: Modifier = Modifier,
    viewModel: SimulationViewModel = viewModel()
) {
    val state by viewModel.simulationState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Icono decorativo (Simulando el del mockup)
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(ProjectileBlue.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("⊿", fontSize = 24.sp, color = ProjectileBlue)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Tiro Parabólico",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Colisión de dos cuerpos",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                actions = {
                    Button(
                        onClick = { viewModel.applyMonkeyHunterPreset() },
                        modifier = Modifier.padding(end = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        shape = CircleShape,
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "⟳ Preset",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )
        },
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // ========== Banner de Mensaje (Solo si hay colisión) ==========
            state.collisionInfo?.let { collision ->
                if (collision.occurred) {
                    CollisionBanner(collisionInfo = collision)
                }
            }

            // ========== Canvas de Simulación ==========
            SimulationCanvas(
                currentTime = state.currentTime,
                projectilePos = state.projectilePos,
                targetPos = state.targetPos,
                projectileTrajectory = state.projectileTrajectory,
                targetTrajectory = state.targetTrajectory,
                collisionInfo = state.collisionInfo,
                maxSimulationTime = state.maxSimulationTime,
                modifier = Modifier.padding(16.dp)
            )

            // ========== Readouts de Posición ==========
            PositionReadout(
                currentTime = state.currentTime,
                projectilePos = state.projectilePos,
                projectileSpeed = state.projectileSpeed,
                targetPos = state.targetPos,
                targetSpeed = state.targetSpeed,
                distance = state.currentDistance
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ========== Controles de Transporte ==========
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                TransportControls(
                    isRunning = state.isRunning,
                    onPlayPause = {
                        if (state.isRunning) viewModel.pause()
                        else viewModel.play()
                    },
                    onReset = { viewModel.reset() }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ========== Custom Pill Tabs ==========
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        shape = CircleShape
                    )
                    .padding(4.dp)
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    TabPill(
                        label = "Parámetros",
                        isSelected = selectedTab == 0,
                        modifier = Modifier.weight(1f),
                        onClick = { selectedTab = 0 }
                    )
                    TabPill(
                        label = "Gráficas",
                        isSelected = selectedTab == 1,
                        modifier = Modifier.weight(1f),
                        onClick = { selectedTab = 1 }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ========== Contenido de Tabs ==========
            when (selectedTab) {
                0 -> {
                    ParameterSliders(
                        params = state.params,
                        onParamChange = { viewModel.updateParams(it) },
                        onMonkeyHunterClick = {
                            val angle = viewModel.calculateMonkeyHunterAngle()
                            viewModel.updateParam { it.copy(projectileAngleDegrees = angle) }
                        }
                    )
                }
                1 -> {
                    TrajectoryChart(
                        projectileTrajectory = state.projectileTrajectory,
                        targetTrajectory = state.targetTrajectory,
                        collisionInfo = state.collisionInfo,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun TabPill(
    label: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(CircleShape)
            .background(if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
