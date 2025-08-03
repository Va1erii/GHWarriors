package jp.vpopov.ghwarriors

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import com.arkivanov.decompose.defaultComponentContext
import dagger.hilt.android.AndroidEntryPoint
import jp.vpopov.ghwarriors.app.RootComponent
import jp.vpopov.ghwarriors.app.RootScreen
import jp.vpopov.ghwarriors.core.designsystem.theme.GHWarriorsTheme
import jp.vpopov.ghwarriors.core.logging.Logging
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
            openExternalUrl = ::openExternalUrl,
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
}