package jp.vpopov.ghwarriors.core.dispatchers.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.vpopov.ghwarriors.core.dispatchers.AppDispatchers
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DispatchersModule {
    @Singleton
    @Provides
    fun providesAppDispatchers(): AppDispatchers {
        return AppDispatchers(
            io = Dispatchers.IO,
            main = Dispatchers.Main,
            default = Dispatchers.Default
        )
    }
}