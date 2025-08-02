package jp.vpopov.ghwarriors

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.arkivanov.decompose.defaultComponentContext
import dagger.hilt.android.AndroidEntryPoint
import jp.vpopov.ghwarriors.app.RootComponent
import jp.vpopov.ghwarriors.app.RootScreen
import jp.vpopov.ghwarriors.core.designsystem.theme.GHWarriorsTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var rootComponentFactory: RootComponent.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val component = rootComponentFactory.create(
            componentContext = defaultComponentContext(),
        )
        setContent {
            GHWarriorsTheme {
                RootScreen(component)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
    }
}