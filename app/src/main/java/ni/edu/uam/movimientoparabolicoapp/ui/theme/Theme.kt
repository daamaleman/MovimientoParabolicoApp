package ni.edu.uam.movimientoparabolicoapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = ProjectileBlue,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = TextDark,
    surface = TextDark,
    surfaceContainer = BorderLight,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = SurfaceLight,
    onSurface = SurfaceLight,
    onSurfaceVariant = TextVariant
)

private val LightColorScheme = lightColorScheme(
    primary = ProjectileBlue,
    secondary = TargetOrange,
    tertiary = CollisionGreen,
    background = Color.White,
    surface = Color.White,
    surfaceContainer = SurfaceLight,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = TextDark,
    onSurface = TextDark,
    onSurfaceVariant = TextVariant
)

@Composable
fun MovimientoParabolicoAppTheme(
    darkTheme: Boolean = true, // Forzado a true por defecto
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // Siempre usamos el esquema oscuro para cumplir con el requerimiento
    val colorScheme = DarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
