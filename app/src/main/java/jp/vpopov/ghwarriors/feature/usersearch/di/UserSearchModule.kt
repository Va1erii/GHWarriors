package jp.vpopov.ghwarriors.feature.usersearch.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import jp.vpopov.ghwarriors.feature.usersearch.data.UserSearchApi
import jp.vpopov.ghwarriors.feature.usersearch.data.SearchRepository
import jp.vpopov.ghwarriors.feature.usersearch.data.SearchRepositoryImpl
import retrofit2.Retrofit

@Module
@InstallIn(ViewModelComponent::class)
class UserSearchModule {
    @Provides
    fun providesSearchApi(
        retrofit: Retrofit
    ): UserSearchApi = retrofit.create(UserSearchApi::class.java)

    @Provides
    @ViewModelScoped
    fun providesSearchRepository(
        userSearchApi: UserSearchApi
    ): SearchRepository = SearchRepositoryImpl(userSearchApi)
}