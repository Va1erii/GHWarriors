package jp.vpopov.ghwarriors.core.logging.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.vpopov.ghwarriors.core.logging.LoggingManager
import jp.vpopov.ghwarriors.core.logging.timber.TimberLoggingManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface LoggingModule {
    @Binds
    @Singleton
    fun bindLoggingManager(manager: TimberLoggingManager): LoggingManager
}