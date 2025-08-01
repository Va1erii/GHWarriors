package jp.vpopov.ghwarriors.core.data.repository.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.vpopov.ghwarriors.core.data.repository.RepositoryApi
import jp.vpopov.ghwarriors.core.data.repository.UserRepository
import jp.vpopov.ghwarriors.core.data.repository.UserRepositoryImpl
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {
    @Singleton
    @Provides
    fun providesRepository(impl: UserRepositoryImpl): UserRepository = impl

    @Provides
    fun providesRepositoryApi(retrofit: Retrofit): RepositoryApi {
        return retrofit.create(RepositoryApi::class.java)
    }
}