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
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ni.edu.uam.movimientoparabolicoapp.data.SimulationParams
import ni.edu.uam.movimientoparabolicoapp.ui.theme.ProjectileBlue
import ni.edu.uam.movimientoparabolicoapp.ui.theme.TargetOrange

/**
 * Panel de sliders para controlar todos los parámetros de la simulación.
 */
@Composable
fun ParameterSliders(
    params: SimulationParams,
    onParamChange: (SimulationParams) -> Unit,
    onMonkeyHunterClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    ) {
        // Sección: Proyectil A
        GroupTitle(
            label = "Objeto A — Proyectil",
            color = ProjectileBlue
        )

        ParameterSlider(
            label = "Velocidad inicial v₀",
            value = params.projectileInitialSpeed,
            minValue = 1.0,
            maxValue = 40.0,
            unit = "m/s",
            color = ProjectileBlue,
            onChange = { newValue ->
                onParamChange(params.copy(projectileInitialSpeed = newValue))
            }
        )

        ParameterSlider(
            label = "Ángulo θ",
            value = params.projectileAngleDegrees,
            minValue = 0.0,
            maxValue = 90.0,
            unit = "°",
            color = ProjectileBlue,
            onChange = { newValue ->
                onParamChange(params.copy(projectileAngleDegrees = newValue))
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Sección: Objetivo B
        GroupTitle(
            label = "Objeto B — Objetivo",
            color = TargetOrange
        )

        ParameterSlider(
            label = "Distancia horizontal",
            value = params.targetX,
            minValue = 1.0,
            maxValue = 30.0,
            unit = "m",
            color = TargetOrange,
            onChange = { newValue ->
                onParamChange(params.copy(targetX = newValue))
            }
        )

        ParameterSlider(
            label = "Altura inicial",
            value = params.targetY,
            minValue = 0.0,
            maxValue = 18.0,
            unit = "m",
            color = TargetOrange,
            onChange = { newValue ->
                onParamChange(params.copy(targetY = newValue))
            }
        )

        ParameterSlider(
            label = "Velocidad inicial v₀ (cae si =0)",
            value = params.targetInitialSpeed,
            minValue = 0.0,
            maxValue = 20.0,
            unit = "m/s",
            color = TargetOrange,
            onChange = { newValue ->
                onParamChange(params.copy(targetInitialSpeed = newValue))
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Sección: Entorno
        GroupTitle(
            label = "Entorno",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        ParameterSlider(
            label = "Gravedad g",
            value = params.gravity,
            minValue = 1.6,
            maxValue = 24.8,
            unit = "m/s²",
            color = ProjectileBlue,
            onChange = { newValue ->
                onParamChange(params.copy(gravity = newValue))
            }
        )

        ParameterSlider(
            label = "Velocidad de animación",
            value = params.animationSpeedMultiplier,
            minValue = 0.2,
            maxValue = 3.0,
            unit = "×",
            color = ProjectileBlue,
            onChange = { newValue ->
                onParamChange(params.copy(animationSpeedMultiplier = newValue))
            }
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun GroupTitle(
    label: String,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(14.dp)
                .background(color = color, shape = RoundedCornerShape(4.dp))
        )
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun ParameterSlider(
    label: String,
    value: Double,
    minValue: Double,
    maxValue: Double,
    unit: String,
    color: Color,
    onChange: (Double) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${String.format("%.1f", value)} $unit",
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Slider(
                value = value.toFloat(),
                onValueChange = { newValue ->
                    onChange(newValue.toDouble())
                },
                valueRange = minValue.toFloat()..maxValue.toFloat(),
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = color,
                    activeTrackColor = color,
                    inactiveTrackColor = color.copy(alpha = 0.1f)
                )
            )
        }
    }
}
