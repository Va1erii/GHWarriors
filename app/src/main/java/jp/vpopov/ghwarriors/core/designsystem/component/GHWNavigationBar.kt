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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import jp.vpopov.ghwarriors.R
import jp.vpopov.ghwarriors.core.designsystem.theme.GHWarriorsTheme
import jp.vpopov.ghwarriors.core.extension.Localization
import jp.vpopov.ghwarriors.util.ImageResource
import jp.vpopov.ghwarriors.util.ImageResource.VectorImage
import jp.vpopov.ghwarriors.util.ImageResource.VectorResource
import jp.vpopov.ghwarriors.util.imageVector

@Immutable
data class NavItem(
    @StringRes val title: Int,
    val selectedIcon: ImageResource,
    val unselectedIcon: ImageResource,
)

@Composable
fun GHWNavigationBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    items: List<NavItem>,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        modifier = modifier,
    ) {
        val itemSelected by rememberUpdatedState(onItemSelected)
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    val icon = if (selectedIndex == index) {
                        item.selectedIcon
                    } else {
                        item.unselectedIcon
                    }
                    Icon(
                        icon.imageVector(),
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
                selected = selectedIndex == index,
                onClick = { itemSelected(index) }
            )
        }
    }
}

@Preview
@Composable
private fun GHWNavigationBarPreview() {
    val items = listOf(
        NavItem(
            title = Localization.search,
            selectedIcon = VectorImage(Icons.Filled.Home),
            unselectedIcon = VectorImage(Icons.Outlined.Home),
        ),
        NavItem(
            title = Localization.bookmarks,
            selectedIcon = VectorResource(R.drawable.ic_bookmark_filled),
            unselectedIcon = VectorResource(R.drawable.ic_bookmark_outlined)
        ),
        NavItem(
            title = Localization.settings,
            selectedIcon = VectorImage(Icons.Filled.Settings),
            unselectedIcon = VectorImage(Icons.Outlined.Settings)
        )
    )
    GHWarriorsTheme {
        var selectedIndex by rememberSaveable { mutableIntStateOf(0) }
        Scaffold(
            bottomBar = {
                GHWNavigationBar(
                    selectedIndex = selectedIndex,
                    onItemSelected = {
                        selectedIndex = it
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