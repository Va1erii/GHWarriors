package jp.vpopov.ghwarriors.app.tabs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.displayCutoutPadding
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
import jp.vpopov.ghwarriors.core.designsystem.component.GHWNavigationBar
import jp.vpopov.ghwarriors.core.designsystem.component.NavItem
import jp.vpopov.ghwarriors.core.extension.Localization
import jp.vpopov.ghwarriors.feature.bookmark.presentation.ui.BookmarkContent
import jp.vpopov.ghwarriors.feature.search.presentation.ui.SearchContent
import jp.vpopov.ghwarriors.util.PainterResource.DrawableResource
import jp.vpopov.ghwarriors.util.PainterResource.VectorImage

private val navItems = TabsComponent.Tab.entries
    .sortedBy { it.index }
    .map { tab -> tab to tab.createNavItem() }

@Composable
fun TabsScreen(
    component: TabsComponent,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        bottomBar = {
            val stack by component.stack.subscribeAsState()
            val activeComponent = stack.active.instance
            GHWNavigationBar(
                selectedTab = activeComponent.tab,
                onItemSelected = { tab ->
                    component.selectTab(tab)
                },
                items = navItems
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Children(
            stack = component.stack,
            animation = stackAnimation(),
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .displayCutoutPadding()
                .fillMaxSize(),
        ) { child ->
            when (val instance = child.instance) {
                is TabsComponent.Child.Search -> SearchContent(
                    component = instance.component,
                    modifier = Modifier.fillMaxSize()
                )

                is TabsComponent.Child.Bookmark -> BookmarkContent(
                    component = instance.component,
                    modifier = Modifier.fillMaxSize()
                )

                is TabsComponent.Child.Settings -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Settings")
                }
            }
        }
    }
}

private fun TabsComponent.Tab.createNavItem(): NavItem {
    return when (this) {
        TabsComponent.Tab.Search -> NavItem(
            title = Localization.search,
            selectedIcon = VectorImage(Icons.Filled.Home),
            unselectedIcon = VectorImage(Icons.Outlined.Home),
        )

        TabsComponent.Tab.Bookmarks -> NavItem(
            title = Localization.bookmarks,
            selectedIcon = DrawableResource(R.drawable.ic_bookmark_filled),
            unselectedIcon = DrawableResource(R.drawable.ic_bookmark_outlined),
        )

        TabsComponent.Tab.Settings -> NavItem(
            title = Localization.settings,
            selectedIcon = VectorImage(Icons.Filled.Settings),
            unselectedIcon = VectorImage(Icons.Outlined.Settings),
        )
    }
}