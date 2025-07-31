package jp.vpopov.ghwarriors

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.arkivanov.decompose.defaultComponentContext
import dagger.hilt.android.AndroidEntryPoint
import jp.vpopov.ghwarriors.core.designsystem.theme.GHWarriorsTheme
import jp.vpopov.ghwarriors.feature.search.presentation.component.SearchComponent
import jp.vpopov.ghwarriors.feature.search.presentation.ui.SearchScreen
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var searchComponentFactory: SearchComponent.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val component = searchComponentFactory.create(
            componentContext = defaultComponentContext(),
            userSelected = { user -> }
        )
        setContent {
            GHWarriorsTheme {
                SearchScreen(component = component)
            }
        }
    }
}