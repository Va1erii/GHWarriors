package jp.vpopov.ghwarriors.app.search

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import jp.vpopov.ghwarriors.app.search.SearchRootComponent.Child
import jp.vpopov.ghwarriors.feature.profile.presentation.ui.ProfileScreen
import jp.vpopov.ghwarriors.feature.search.presentation.ui.SearchContent

@Composable
fun SearchRootContent(
    component: SearchRootComponent,
    modifier: Modifier = Modifier
) {
    Children(
        component.childStack,
        modifier = modifier
    ) {
        when (val child = it.instance) {
            is Child.Profile -> ProfileScreen(child.component)
            is Child.Search -> SearchContent(child.component, modifier = Modifier.fillMaxSize())
        }
    }
}