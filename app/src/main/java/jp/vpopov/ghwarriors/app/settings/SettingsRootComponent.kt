package jp.vpopov.ghwarriors.app.settings

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import jakarta.inject.Inject

interface SettingsRootComponent {
    interface Factory {
        fun create(
            componentContext: ComponentContext,
        ): SettingsRootComponent
    }
}

class DefaultSettingsRootComponentFactory @Inject constructor() : SettingsRootComponent.Factory {
    override fun create(
        componentContext: ComponentContext
    ): SettingsRootComponent {
        return DefaultSettingsRootComponent(componentContext)
    }

}

class DefaultSettingsRootComponent(
    componentContext: ComponentContext
) : SettingsRootComponent, ComponentContext by componentContext {
}