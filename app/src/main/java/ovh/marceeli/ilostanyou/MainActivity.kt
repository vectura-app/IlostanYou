package ovh.marceeli.ilostanyou

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import ovh.marceeli.ilostanyou.ui.Navigation
import ovh.marceeli.ilostanyou.ui.theme.IlostanTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            IlostanTheme {
                Navigation()
            }
        }
    }
}