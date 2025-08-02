package jp.vpopov.ghwarriors.feature.search.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.components.ViewModelComponent
import jp.vpopov.ghwarriors.feature.search.presentation.component.DefaultSearchComponentFactory
import jp.vpopov.ghwarriors.feature.search.presentation.component.SearchComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
class SearchModule {
    @Provides
    fun providesSearchComponentFactory(
        impl: DefaultSearchComponentFactory
    ): SearchComponent.Factory = impl
}