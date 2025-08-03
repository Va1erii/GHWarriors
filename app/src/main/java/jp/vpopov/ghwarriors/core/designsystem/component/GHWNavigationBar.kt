package jp.vpopov.ghwarriors.core.designsystem.component

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import jp.vpopov.ghwarriors.R
import jp.vpopov.ghwarriors.app.RootComponent.Tab
import jp.vpopov.ghwarriors.core.designsystem.theme.GHWarriorsTheme
import jp.vpopov.ghwarriors.core.extension.Localization
import jp.vpopov.ghwarriors.util.PainterResource
import jp.vpopov.ghwarriors.util.PainterResource.VectorImage
import jp.vpopov.ghwarriors.util.PainterResource.DrawableResource
import jp.vpopov.ghwarriors.util.painter

@Immutable
data class NavItem(
    @StringRes val title: Int,
    val selectedIcon: PainterResource,
    val unselectedIcon: PainterResource,
)

@Composable
fun <Tab> GHWNavigationBar(
    selectedTab: Tab,
    onItemSelected: (Tab) -> Unit,
    items: List<Pair<Tab, NavItem>>,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        modifier = modifier,
    ) {
        val itemSelected by rememberUpdatedState(onItemSelected)
        items.forEach { (tab, item) ->
            NavigationBarItem(
                icon = {
                    val icon = if (selectedTab == tab) {
                        item.selectedIcon
                    } else {
                        item.unselectedIcon
                    }
                    Icon(
                        icon.painter(),
                        tint = MaterialTheme.colorScheme.onSurface,
                        contentDescription = stringResource(item.title)
                    )
                },
                label = {
                    Text(
                        text = stringResource(item.title),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                selected = selectedTab == tab,
                onClick = { itemSelected(tab) }
            )
        }
    }
}

@Preview
@Composable
private fun GHWNavigationBarPreview() {
    val items = listOf(
        Tab.Search to NavItem(
            title = Localization.search,
            selectedIcon = VectorImage(Icons.Filled.Home),
            unselectedIcon = VectorImage(Icons.Outlined.Home),
        ),
        Tab.Bookmarks to NavItem(
            title = Localization.bookmarks,
            selectedIcon = DrawableResource(R.drawable.ic_bookmark_filled),
            unselectedIcon = DrawableResource(R.drawable.ic_bookmark_outlined)
        ),
        Tab.Settings to NavItem(
            title = Localization.settings,
            selectedIcon = VectorImage(Icons.Filled.Settings),
            unselectedIcon = VectorImage(Icons.Outlined.Settings)
        )
    )
    GHWarriorsTheme {
        var selectedTab by rememberSaveable { mutableStateOf(Tab.Search) }
        Scaffold(
            bottomBar = {
                GHWNavigationBar(
                    selectedTab = selectedTab,
                    onItemSelected = {
                        selectedTab = it
                    },
                    items = items
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        }
    }
}