package jp.vpopov.ghwarriors.feature.settings.presentation.component

import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import jp.vpopov.ghwarriors.core.decompose.DecomposeViewModel
import jp.vpopov.ghwarriors.core.dispatchers.AppDispatchers
import jp.vpopov.ghwarriors.core.domain.model.ThemeConfig
import jp.vpopov.ghwarriors.core.logging.Logging
import jp.vpopov.ghwarriors.core.logging.e
import jp.vpopov.ghwarriors.core.logging.withTagLazy
import jp.vpopov.ghwarriors.core.preferences.AppPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsState(
    val theme: ThemeConfig = ThemeConfig.FOLLOW_SYSTEM,
    val themeOptions: List<ThemeConfig> = ThemeConfig.entries.toList()
)

class SettingsViewModel @AssistedInject constructor(
    private val preferences: AppPreferences,
    dispatchers: AppDispatchers
) : DecomposeViewModel(dispatchers) {
    private val logger by Logging.withTagLazy(this::class)
    val state: StateFlow<SettingsState> = preferences.observeTheme()
        .map { SettingsState(theme = it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, SettingsState())

    fun setThemePreference(theme: ThemeConfig) {
        viewModelScope.launch {
            runCatching {
                preferences.setTheme(theme)
            }.onFailure { exception ->
                logger.e(exception) { "Failed to set theme preference: ${theme.name}" }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(): SettingsViewModel
    }
}