package jp.vpopov.ghwarriors.feature.profile.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import jp.vpopov.ghwarriors.feature.profile.presentation.component.DefaultProfileComponentFactory
import jp.vpopov.ghwarriors.feature.profile.presentation.component.ProfileComponent

@Module
@InstallIn(ViewModelComponent::class)
class ProfileModule {
    @Provides
    fun providesProfileComponentFactory(
        impl: DefaultProfileComponentFactory
    ): ProfileComponent.Factory = impl
}