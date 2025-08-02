package jp.vpopov.ghwarriors.app.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.components.ViewModelComponent
import jp.vpopov.ghwarriors.app.DefaultRootComponentFactory
import jp.vpopov.ghwarriors.app.RootComponent
import jp.vpopov.ghwarriors.app.bookmark.BookmarkRootComponent
import jp.vpopov.ghwarriors.app.bookmark.DefaultBookmarkRootComponentFactory
import jp.vpopov.ghwarriors.app.search.DefaultSearchRootComponentFactory
import jp.vpopov.ghwarriors.app.search.SearchRootComponent
import jp.vpopov.ghwarriors.app.settings.DefaultSettingsRootComponentFactory
import jp.vpopov.ghwarriors.app.settings.SettingsRootComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
interface AppModule {
    @Binds
    fun bindsSearchRootComponentFactory(
        impl: DefaultSearchRootComponentFactory
    ): SearchRootComponent.Factory

    @Binds
    fun bindsBookmarkRootComponentFactory(
        impl: DefaultBookmarkRootComponentFactory
    ): BookmarkRootComponent.Factory

    @Binds
    fun bindsSettingsRootComponentFactory(
        impl: DefaultSettingsRootComponentFactory
    ): SettingsRootComponent.Factory

    @Binds
    fun providesRootComponentFactory(
        impl: DefaultRootComponentFactory
    ): RootComponent.Factory
}