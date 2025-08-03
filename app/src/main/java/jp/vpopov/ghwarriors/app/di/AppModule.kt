package jp.vpopov.ghwarriors.app.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import jp.vpopov.ghwarriors.app.DefaultRootComponentFactory
import jp.vpopov.ghwarriors.app.RootComponent
import jp.vpopov.ghwarriors.app.settings.DefaultSettingsRootComponentFactory
import jp.vpopov.ghwarriors.app.settings.SettingsRootComponent
import jp.vpopov.ghwarriors.app.tabs.DefaultTabsComponentFactory
import jp.vpopov.ghwarriors.app.tabs.TabsComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
interface AppModule {
    @Binds
    fun bindsSettingsRootComponentFactory(
        impl: DefaultSettingsRootComponentFactory
    ): SettingsRootComponent.Factory

    @Binds
    fun providesRootComponentFactory(
        impl: DefaultRootComponentFactory
    ): RootComponent.Factory

    @Binds
    fun providesTabsComponentFactory(
        impl: DefaultTabsComponentFactory
    ): TabsComponent.Factory
}