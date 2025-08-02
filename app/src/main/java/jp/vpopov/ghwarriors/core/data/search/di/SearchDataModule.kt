package jp.vpopov.ghwarriors.core.data.search.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.vpopov.ghwarriors.core.data.search.SearchApi
import jp.vpopov.ghwarriors.core.data.search.SearchRepository
import jp.vpopov.ghwarriors.core.data.search.SearchRepositoryImpl
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SearchDataModule {
    @Provides
    fun providesSearchApi(
        retrofit: Retrofit
    ): SearchApi = retrofit.create(SearchApi::class.java)

    @Provides
    @Singleton
    fun providesSearchRepository(
        searchApi: SearchApi
    ): SearchRepository = SearchRepositoryImpl(searchApi)
}