package jp.vpopov.ghwarriors.feature.profile.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.vpopov.ghwarriors.feature.profile.data.ProfileApi
import jp.vpopov.ghwarriors.feature.profile.data.ProfileRepository
import jp.vpopov.ghwarriors.feature.profile.data.ProfileRepositoryImpl
import jp.vpopov.ghwarriors.feature.profile.presentation.component.DefaultProfileComponentFactory
import jp.vpopov.ghwarriors.feature.profile.presentation.component.ProfileComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ProfileModule {
    @Provides
    fun providesProfileApi(retrofit: Retrofit): ProfileApi {
        return retrofit.create(ProfileApi::class.java)
    }

    @Provides
    @Singleton
    fun providesProfileRepository(
        impl: ProfileRepositoryImpl
    ): ProfileRepository = impl

    @Provides
    fun providesProfileComponentFactory(
        impl: DefaultProfileComponentFactory
    ): ProfileComponent.Factory = impl
}