package jp.vpopov.ghwarriors.feature.settings.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import jp.vpopov.ghwarriors.feature.settings.presentation.component.DefaultSettingsComponentFactory
import jp.vpopov.ghwarriors.feature.settings.presentation.component.SettingsComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class SettingsModule {

    @Binds
    abstract fun bindSettingsComponentFactory(
        factory: DefaultSettingsComponentFactory
    ): SettingsComponent.Factory
}