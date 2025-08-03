package jp.vpopov.ghwarriors.core.data.user.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.vpopov.ghwarriors.core.data.user.MockUserRepository
import jp.vpopov.ghwarriors.core.data.user.UserApi
import jp.vpopov.ghwarriors.core.data.user.UserRepository
import jp.vpopov.ghwarriors.core.data.user.UserRepositoryImpl
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UserDataModule {
    @Provides
    fun providesUserApi(retrofit: Retrofit): UserApi {
        return retrofit.create(UserApi::class.java)
    }

    @Provides
    @Singleton
    fun providesUserRepository(impl: UserRepositoryImpl): UserRepository = impl
}