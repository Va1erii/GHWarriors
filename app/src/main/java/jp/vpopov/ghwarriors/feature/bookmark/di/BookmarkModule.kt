package jp.vpopov.ghwarriors.feature.bookmark.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import jp.vpopov.ghwarriors.feature.bookmark.presentation.component.BookmarkComponent
import jp.vpopov.ghwarriors.feature.bookmark.presentation.component.DefaultBookmarkComponentFactory

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class BookmarkModule {
    @Binds
    abstract fun bindBookmarkComponentFactory(
        factory: DefaultBookmarkComponentFactory
    ): BookmarkComponent.Factory
}