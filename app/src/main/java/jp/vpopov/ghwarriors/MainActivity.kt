package jp.vpopov.ghwarriors

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.arkivanov.decompose.defaultComponentContext
import dagger.hilt.android.AndroidEntryPoint
import jp.vpopov.ghwarriors.app.search.SearchRootComponent
import jp.vpopov.ghwarriors.app.search.SearchRootContent
import jp.vpopov.ghwarriors.core.designsystem.theme.GHWarriorsTheme
import jp.vpopov.ghwarriors.feature.profile.presentation.component.ProfileComponent
import jp.vpopov.ghwarriors.feature.search.presentation.component.SearchComponent
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var searchComponentFactory: SearchComponent.Factory

    @Inject
    lateinit var profileComponentFactory: ProfileComponent.Factory

    @Inject
    lateinit var searchRootComponentFactory: SearchRootComponent.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val component = searchRootComponentFactory.create(
            componentContext = defaultComponentContext(),
        )
        setContent {
            GHWarriorsTheme {
                SearchRootContent(component)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
    }
}