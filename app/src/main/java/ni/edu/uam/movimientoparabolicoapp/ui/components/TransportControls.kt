package ni.edu.uam.movimientoparabolicoapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Controles de transporte: Iniciar/Pausar, Reiniciar.
 */
@Composable
fun TransportControls(
    isRunning: Boolean,
    onPlayPause: () -> Unit,
    onReset: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Botón Play/Pause
        Button(
            onClick = onPlayPause,
            modifier = Modifier
                .weight(1f)
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isRunning)
                    Color(0xFF00897b)  // Verde (running)
                else
                    MaterialTheme.colorScheme.primary,  // Azul (paused/stopped)
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(100.dp)
        ) {
            Text(
                text = if (isRunning) "❚❚  Detener" else "▶  Iniciar",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (0.5).sp
            )
        }

        // Botón Reset
        Button(
            onClick = onReset,
            modifier = Modifier
                .size(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            shape = RoundedCornerShape(100.dp)
        ) {
            Text(
                text = "⟲",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

