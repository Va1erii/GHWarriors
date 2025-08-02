package jp.vpopov.ghwarriors.core.data.userrepo.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.vpopov.ghwarriors.core.data.userrepo.UserRepoApi
import jp.vpopov.ghwarriors.core.data.userrepo.UserRepoInfoRepository
import jp.vpopov.ghwarriors.core.data.userrepo.UserRepoInfoRepositoryImpl
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UserRepoDataModule {
    @Singleton
    @Provides
    fun providesRepository(impl: UserRepoInfoRepositoryImpl): UserRepoInfoRepository = impl

    @Provides
    fun providesRepositoryApi(retrofit: Retrofit): UserRepoApi {
        return retrofit.create(UserRepoApi::class.java)
    }
}