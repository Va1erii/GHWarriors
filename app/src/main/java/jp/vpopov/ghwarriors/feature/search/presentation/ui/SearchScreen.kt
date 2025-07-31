package jp.vpopov.ghwarriors.feature.search.presentation.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import jp.vpopov.ghwarriors.feature.search.presentation.component.SearchComponent

@Composable
fun SearchScreen(
    component: SearchComponent
) {
    SearchContent(
        component = component,
        modifier = Modifier.fillMaxSize()
    )
}