package jp.vpopov.ghwarriors.feature.shared.presentation

import androidx.annotation.StringRes
import kotlinx.coroutines.flow.StateFlow

interface TopBarComponent {
    val model: StateFlow<Model>

    data class Model(
        @StringRes val title: Int,
        val actions: List<TopBarAction>,
    )
}

interface TopBarAction {
}