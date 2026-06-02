package ni.edu.uam.movimientoparabolicoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ni.edu.uam.movimientoparabolicoapp.ui.SimulationScreen
import ni.edu.uam.movimientoparabolicoapp.ui.theme.MovimientoParabolicoAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MovimientoParabolicoAppTheme {
                SimulationScreen(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SimulationScreenPreview() {
    MovimientoParabolicoAppTheme {
        SimulationScreen()
    }
}