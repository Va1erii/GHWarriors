package jp.vpopov.ghwarriors

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.arkivanov.decompose.defaultComponentContext
import dagger.hilt.android.AndroidEntryPoint
import jp.vpopov.ghwarriors.app.RootComponent
import jp.vpopov.ghwarriors.app.RootScreen
import jp.vpopov.ghwarriors.core.designsystem.theme.GHWarriorsTheme
import jp.vpopov.ghwarriors.core.domain.model.ThemeConfig
import jp.vpopov.ghwarriors.core.logging.Logging
import jp.vpopov.ghwarriors.core.preferences.AppPreferences
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var rootComponentFactory: RootComponent.Factory

    @Inject
    lateinit var preferences: AppPreferences
    private var component: RootComponent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var themeConfig by mutableStateOf(ThemeConfig.FOLLOW_SYSTEM)
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                preferences.observeTheme()
                    .distinctUntilChanged()
                    .onEach { themeConfig = it }
                    .map { it.isDarkTheme() }
                    .collectLatest { darkTheme ->
                        // Turn off the decor fitting system windows, which allows us to handle insets,
                        // including IME animations, and go edge-to-edge.
                        // This is the same parameters as the default enableEdgeToEdge call, but we manually
                        // resolve whether or not to show dark theme using uiState, since it can be different
                        // than the configuration's dark theme value based on the user preference.
                        enableEdgeToEdge(
                            statusBarStyle = SystemBarStyle.auto(
                                lightScrim = Color.TRANSPARENT,
                                darkScrim = Color.TRANSPARENT
                            ) { darkTheme },
                            navigationBarStyle = SystemBarStyle.auto(
                                lightScrim = Color.TRANSPARENT,
                                darkScrim = Color.TRANSPARENT
                            ) { darkTheme },
                        )
                    }
            }
        }
        val deeplinkUrl = intent.dataString
        val component = rootComponentFactory.create(
            componentContext = defaultComponentContext(),
            openExternalUrl = ::openExternalUrl,
            deeplinkUrl = deeplinkUrl
        ).also { this.component = it }
        setContent {
            GHWarriorsTheme(
                darkTheme = themeConfig.isDarkTheme()
            ) {
                RootScreen(component)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val deeplinkUrl = intent.dataString ?: return
        component?.handleDeeplink(deeplinkUrl)
    }

    private fun openExternalUrl(url: String) {
        val uri = runCatching { url.toUri() }
            .onFailure { Logging.e(it) { "Cannot parse URL: $url" } }
            .getOrNull()
            ?: return
        val isCustomTabSupported = CustomTabsClient.getPackageName(this, emptyList()) != null
        if (isCustomTabSupported) {
            openCustomTab(uri)
        } else {
            openWithIntent(uri)
        }
    }

    private fun openCustomTab(uri: Uri) {
        val customTabsIntent = CustomTabsIntent.Builder()
            .setShareState(CustomTabsIntent.SHARE_STATE_ON)
            .build()
        val intent = customTabsIntent.intent
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.data = uri
        runCatching {
            startActivity(intent, customTabsIntent.startAnimationBundle)
        }.onFailure {
            Logging.e(it) { "Couldn't open custom tab" }
        }
    }

    private fun openWithIntent(uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        runCatching {
            startActivity(intent)
        }.onFailure {
            Logging.e(it) { "Couldn't open with intent" }
        }
    }

    private fun ThemeConfig.isDarkTheme(): Boolean {
        return when (this) {
            ThemeConfig.LIGHT -> false
            ThemeConfig.DARK -> true
            ThemeConfig.FOLLOW_SYSTEM -> resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        }
    }
}