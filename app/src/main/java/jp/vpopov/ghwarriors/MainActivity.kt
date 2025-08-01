package jp.vpopov.ghwarriors

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
                Scaffold { innerPadding ->
                    SearchRootContent(
                        component = component,
                        modifier = Modifier
                            .background(Color.Cyan)
                            .padding(innerPadding)
                            .fillMaxSize()
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
    }
}