package jp.vpopov.ghwarriors.feature.search.presentation.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import jp.vpopov.ghwarriors.feature.search.presentation.component.SearchComponent

@Composable
fun SearchScreen(
    component: SearchComponent
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        SearchContent(
            component = component,
            modifier = Modifier.padding(innerPadding)
        )
    }
}