package jp.vpopov.ghwarriors.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.vpopov.ghwarriors.app.search.DefaultSearchRootComponentFactory
import jp.vpopov.ghwarriors.app.search.SearchRootComponent

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    fun providesSearchRootComponentFactory(
        impl: DefaultSearchRootComponentFactory
    ): SearchRootComponent.Factory = impl
}