package ni.edu.uam.movimientoparabolicoapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.graphics.Color
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

/**
 * Pantalla principal de la simulación.
 *
 * Organiza todos los componentes en una interfaz limpia con:
 * - Canvas de simulación
 * - Readouts de posición
 * - Controles de transporte
 * - Tabs para Parámetros y Gráficas
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
                    Column {
                        Text(
                            text = "Tiro Parabólico",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Colisión de dos cuerpos",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                actions = {
                    // Botón de preset
                    Button(
                        onClick = { viewModel.applyMonkeyHunterPreset() },
                        modifier = Modifier.padding(end = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer
                        )
                    ) {
                        Text(
                            text = "⟳ Preset",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .background(color = MaterialTheme.colorScheme.surface)
        ) {
            // ========== Canvas de Simulación ==========
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .background(
                        color = Color(0xFFf0f7fd),
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                SimulationCanvas(
                    currentTime = state.currentTime,
                    projectilePos = state.projectilePos,
                    targetPos = state.targetPos,
                    projectileTrajectory = state.projectileTrajectory,
                    targetTrajectory = state.targetTrajectory,
                    collisionInfo = state.collisionInfo,
                    maxSimulationTime = state.maxSimulationTime,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // ========== Banner de Colisión ==========
            state.collisionInfo?.let { collision ->
                if (collision.occurred) {
                    CollisionBanner(collisionInfo = collision)
                }
            }

            // ========== Readouts de Posición ==========
            PositionReadout(
                currentTime = state.currentTime,
                projectilePos = state.projectilePos,
                projectileSpeed = state.projectileSpeed,
                targetPos = state.targetPos,
                targetSpeed = state.targetSpeed,
                distance = state.currentDistance
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ========== Controles de Transporte ==========
            Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
                TransportControls(
                    isRunning = state.isRunning,
                    onPlayPause = {
                        if (state.isRunning) {
                            viewModel.pause()
                        } else {
                            if (state.collisionInfo != null) {
                                viewModel.reset()
                            } else {
                                viewModel.play()
                            }
                        }
                    },
                    onReset = {
                        viewModel.reset()
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ========== Tabs: Parámetros vs Gráficas ==========
            Box(modifier = Modifier.padding(horizontal = 12.dp)) {
                TabRow(
                    selectedTabIndex = selectedTab,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            shape = RoundedCorkerRadius(24.dp)
                        )
                        .padding(4.dp),
                    containerColor = Color.Transparent,
                    indicator = {}  // Sin indicador por defecto
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                text = "Parámetros",
                                fontSize = 13.sp,
                                fontWeight = if (selectedTab == 0) FontWeight.SemiBold else FontWeight.Normal
                            )
                        },
                        modifier = if (selectedTab == 0) {
                            Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(20.dp)
                                )
                        } else {
                            Modifier
                        }
                    )

                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                text = "Gráficas",
                                fontSize = 13.sp,
                                fontWeight = if (selectedTab == 1) FontWeight.SemiBold else FontWeight.Normal
                            )
                        },
                        modifier = if (selectedTab == 1) {
                            Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(20.dp)
                                )
                        } else {
                            Modifier
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ========== Contenido de Tabs ==========
            when (selectedTab) {
                0 -> {
                    // Panel de Parámetros
                    ParameterSliders(
                        params = state.params,
                        onParamChange = { newParams ->
                            viewModel.updateParams(newParams)
                        },
                        onMonkeyHunterClick = {
                            val angle = viewModel.calculateMonkeyHunterAngle()
                            viewModel.updateParam { it.copy(projectileAngleDegrees = angle) }
                        }
                    )
                }

                1 -> {
                    // Panel de Gráficas
                    TrajectoryChart(
                        projectileTrajectory = state.projectileTrajectory,
                        targetTrajectory = state.targetTrajectory,
                        collisionInfo = state.collisionInfo,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun RoundedCorkerRadius(radius: androidx.compose.ui.unit.Dp): RoundedCornerShape {
    return RoundedCornerShape(radius)
}




