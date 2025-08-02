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
    private var component: RootComponent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val deeplinkUrl = intent.dataString
        val component = rootComponentFactory.create(
            componentContext = defaultComponentContext(),
            deeplinkUrl = deeplinkUrl
        ).also { this.component = it }
        setContent {
            GHWarriorsTheme {
                RootScreen(component)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val deeplinkUrl = intent.dataString ?: return
        component?.handleDeeplink(deeplinkUrl)
    }
}