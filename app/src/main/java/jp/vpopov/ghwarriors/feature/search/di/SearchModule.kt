package jp.vpopov.ghwarriors.feature.search.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.vpopov.ghwarriors.feature.search.data.SearchApi
import jp.vpopov.ghwarriors.feature.search.data.SearchRepository
import jp.vpopov.ghwarriors.feature.search.data.SearchRepositoryImpl
import jp.vpopov.ghwarriors.feature.search.presentation.component.DefaultSearchComponentFactory
import jp.vpopov.ghwarriors.feature.search.presentation.component.SearchComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SearchModule {
    @Provides
    fun providesSearchApi(
        retrofit: Retrofit
    ): SearchApi = retrofit.create(SearchApi::class.java)

    @Provides
    @Singleton
    fun providesSearchRepository(
        searchApi: SearchApi
    ): SearchRepository = SearchRepositoryImpl(searchApi)

    @Provides
    fun providesSearchComponentFactory(
        impl: DefaultSearchComponentFactory
    ): SearchComponent.Factory = impl
}