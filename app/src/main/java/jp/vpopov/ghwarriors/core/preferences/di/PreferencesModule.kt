package jp.vpopov.ghwarriors.core.preferences.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.vpopov.ghwarriors.core.preferences.AppPreferences
import jp.vpopov.ghwarriors.core.preferences.AppPreferencesDataStore
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface PreferencesModule {
    @Singleton
    @Provides
    fun provideAppPreferences(preferences: AppPreferencesDataStore): AppPreferences
}