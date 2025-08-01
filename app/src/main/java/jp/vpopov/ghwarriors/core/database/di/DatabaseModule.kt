package jp.vpopov.ghwarriors.core.database.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jp.vpopov.ghwarriors.core.database.GHWDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): GHWDatabase {
        return Room.databaseBuilder(
            context = context,
            klass = GHWDatabase::class.java,
            name = "ghw_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideUserInfoDao(database: GHWDatabase) = database.userInfoDao()
}