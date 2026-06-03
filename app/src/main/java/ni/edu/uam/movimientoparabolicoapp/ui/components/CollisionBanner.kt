package ni.edu.uam.movimientoparabolicoapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ni.edu.uam.movimientoparabolicoapp.domain.CollisionInfo

/**
 * Banner que se muestra cuando hay colisión.
 * Muestra el tiempo y distancia de la colisión.
 */
@Composable
fun CollisionBanner(collisionInfo: CollisionInfo?) {
    if (collisionInfo != null && collisionInfo.occurred) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color(0xFF2E7D32)) // Verde oscuro para éxito de colisión
                .padding(horizontal = 16.dp, vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🎯", fontSize = 20.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "¡COLISIÓN DETECTADA! t = ${String.format("%.2f", collisionInfo.time)} s",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

