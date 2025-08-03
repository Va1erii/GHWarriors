package jp.vpopov.ghwarriors.feature.settings.presentation.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import jp.vpopov.ghwarriors.core.domain.model.ThemeConfig
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

interface SettingsComponent {
    val model: StateFlow<SettingsState>

    fun setThemePreference(theme: ThemeConfig)

    interface Factory {
        fun create(componentContext: ComponentContext): SettingsComponent
    }
}

class DefaultSettingsComponentFactory @Inject constructor(
    private val viewModelFactory: SettingsViewModel.Factory
) : SettingsComponent.Factory {
    override fun create(componentContext: ComponentContext): SettingsComponent =
        DefaultSettingsComponent(
            componentContext = componentContext,
            viewModelFactory = viewModelFactory
        )
}

class DefaultSettingsComponent(
    componentContext: ComponentContext,
    private val viewModelFactory: SettingsViewModel.Factory
) : SettingsComponent, ComponentContext by componentContext {

    private val viewModel: SettingsViewModel = instanceKeeper.getOrCreate {
        viewModelFactory.create()
    }
    override val model: StateFlow<SettingsState> = viewModel.state

    override fun setThemePreference(theme: ThemeConfig) {
        viewModel.setThemePreference(theme)
    }
}