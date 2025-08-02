package jp.vpopov.ghwarriors.app

import androidx.compose.foundation.background
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.arkivanov.decompose.extensions.compose.pages.ChildPages
import com.arkivanov.decompose.extensions.compose.pages.PagesScrollAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import jp.vpopov.ghwarriors.R
import jp.vpopov.ghwarriors.app.bookmark.BookmarkRootComponent
import jp.vpopov.ghwarriors.app.search.SearchRootComponent
import jp.vpopov.ghwarriors.app.search.SearchRootContent
import jp.vpopov.ghwarriors.app.settings.SettingsRootComponent
import jp.vpopov.ghwarriors.core.designsystem.component.GHWNavigationBar
import jp.vpopov.ghwarriors.core.designsystem.component.NavItem
import jp.vpopov.ghwarriors.core.extension.Localization
import jp.vpopov.ghwarriors.util.ImageResource.VectorImage
import jp.vpopov.ghwarriors.util.ImageResource.VectorResource

@Composable
fun RootScreen(component: RootComponent) {
    val pages by component.pages.subscribeAsState()
    val items by remember {
        derivedStateOf { pages.items.mapNotNull { it.instance?.createNavItem() } }
    }
    var showNavBar by remember { mutableStateOf(true) }
    Scaffold(
        bottomBar = {
            if (showNavBar) {
                GHWNavigationBar(
                    selectedIndex = pages.selectedIndex,
                    onItemSelected = component::selectPage,
                    items = items
                )
            }
        }
    ) { innerPadding ->
        ChildPages(
            pages = component.pages,
            onPageSelected = component::selectPage,
            scrollAnimation = PagesScrollAnimation.Disabled,
            modifier = Modifier
                .background(Color.Cyan)
                .consumeWindowInsets(innerPadding)
                .padding(innerPadding)
                .fillMaxSize()
        ) { _, page ->
            val showNavBarOnPage by page.showNavBar.subscribeAsState()
            showNavBar = showNavBarOnPage
            when (page) {
                is SearchRootComponent -> SearchRootContent(page, modifier = Modifier.fillMaxSize())

                is BookmarkRootComponent -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Bookmark")
                }

                is SettingsRootComponent -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Settings")
                }
            }
        }
    }
}

private fun RootPageComponent.createNavItem(): NavItem? {
    return when (this) {
        is SearchRootComponent -> NavItem(
            title = Localization.search,
            selectedIcon = VectorImage(Icons.Filled.Home),
            unselectedIcon = VectorImage(Icons.Outlined.Home),
        )

        is BookmarkRootComponent -> NavItem(
            title = Localization.bookmarks,
            selectedIcon = VectorResource(R.drawable.ic_bookmark_filled),
            unselectedIcon = VectorResource(R.drawable.ic_bookmark_outlined),
        )

        is SettingsRootComponent -> NavItem(
            title = Localization.settings,
            selectedIcon = VectorImage(Icons.Filled.Settings),
            unselectedIcon = VectorImage(Icons.Outlined.Settings),
        )

        else -> null
    }
}