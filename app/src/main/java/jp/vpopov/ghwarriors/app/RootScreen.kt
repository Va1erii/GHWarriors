package jp.vpopov.ghwarriors.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import jp.vpopov.ghwarriors.app.tabs.TabsScreen
import jp.vpopov.ghwarriors.feature.profile.presentation.ui.ProfileScreen

@Composable
fun RootScreen(component: RootComponent) {
    Children(
        stack = component.stack,
        animation = stackAnimation(),
        modifier = Modifier.fillMaxSize(),
    ) { child ->
        when (val instance = child.instance) {
            is RootComponent.Child.Tabs -> TabsScreen(
                component = instance.component,
                modifier = Modifier.fillMaxSize()
            )

            is RootComponent.Child.Profile -> ProfileScreen(
                component = instance.component,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}