package jp.vpopov.ghwarriors.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import jp.vpopov.ghwarriors.R
import jp.vpopov.ghwarriors.app.search.SearchRootContent
import jp.vpopov.ghwarriors.core.designsystem.component.GHWNavigationBar
import jp.vpopov.ghwarriors.core.designsystem.component.NavItem
import jp.vpopov.ghwarriors.core.extension.Localization
import jp.vpopov.ghwarriors.util.ImageResource.VectorImage
import jp.vpopov.ghwarriors.util.ImageResource.VectorResource

private val navItems = RootComponent.Tab.entries
    .sortedBy { it.index }
    .map { tab -> tab to tab.createNavItem() }

@Composable
fun RootScreen(component: RootComponent) {
    Scaffold(
        bottomBar = {
            val stack by component.stack.subscribeAsState()
            val activeComponent = stack.active.instance
            val showNavBar by activeComponent.component.showNavBar.subscribeAsState()
            if (showNavBar) {
                GHWNavigationBar(
                    selectedTab = activeComponent.tab,
                    onItemSelected = { tab ->
                        component.selectTab(tab)
                    },
                    items = navItems
                )
            }
        }
    ) { innerPadding ->
        Children(
            stack = component.stack,
            animation = stackAnimation(),
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .fillMaxSize(),
        ) { child ->
            when (val instance = child.instance) {
                is RootComponent.Child.Search -> SearchRootContent(
                    component = instance.component,
                    modifier = Modifier.fillMaxSize()
                )

                is RootComponent.Child.Bookmark -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Bookmark")
                }

                is RootComponent.Child.Settings -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Settings")
                }
            }
        }
    }
}

private fun RootComponent.Tab.createNavItem(): NavItem {
    return when (this) {
        RootComponent.Tab.Search -> NavItem(
            title = Localization.search,
            selectedIcon = VectorImage(Icons.Filled.Home),
            unselectedIcon = VectorImage(Icons.Outlined.Home),
        )

        RootComponent.Tab.Bookmarks -> NavItem(
            title = Localization.bookmarks,
            selectedIcon = VectorResource(R.drawable.ic_bookmark_filled),
            unselectedIcon = VectorResource(R.drawable.ic_bookmark_outlined),
        )

        RootComponent.Tab.Settings -> NavItem(
            title = Localization.settings,
            selectedIcon = VectorImage(Icons.Filled.Settings),
            unselectedIcon = VectorImage(Icons.Outlined.Settings),
        )
    }
}