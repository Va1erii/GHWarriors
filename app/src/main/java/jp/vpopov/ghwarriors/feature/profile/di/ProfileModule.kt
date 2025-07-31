package jp.vpopov.ghwarriors.feature.profile.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.vpopov.ghwarriors.feature.profile.data.MockProfileRepository
import jp.vpopov.ghwarriors.feature.profile.data.ProfileRepository
import jp.vpopov.ghwarriors.feature.profile.presentation.component.DefaultProfileComponentFactory
import jp.vpopov.ghwarriors.feature.profile.presentation.component.ProfileComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ProfileModule {
    @Provides
    @Singleton
    fun providesProfileRepository(
        impl: MockProfileRepository
    ): ProfileRepository = impl

    @Provides
    fun providesProfileComponentFactory(
        impl: DefaultProfileComponentFactory
    ): ProfileComponent.Factory = impl
}