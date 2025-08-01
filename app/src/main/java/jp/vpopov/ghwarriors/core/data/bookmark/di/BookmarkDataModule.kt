package jp.vpopov.ghwarriors.core.data.bookmark.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.vpopov.ghwarriors.core.data.bookmark.BookmarkRepository
import jp.vpopov.ghwarriors.core.data.bookmark.BookmarkRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface BookmarkDataModule {
    @Binds
    @Singleton
    fun bindsBookmarkRepository(impl: BookmarkRepositoryImpl): BookmarkRepository
}