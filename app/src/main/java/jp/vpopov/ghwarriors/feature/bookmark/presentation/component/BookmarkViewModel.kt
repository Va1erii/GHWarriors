package jp.vpopov.ghwarriors.feature.bookmark.presentation.component

import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import jp.vpopov.ghwarriors.core.data.bookmark.BookmarkRepository
import jp.vpopov.ghwarriors.core.decompose.DecomposeViewModel
import jp.vpopov.ghwarriors.core.dispatchers.AppDispatchers
import jp.vpopov.ghwarriors.core.domain.model.UserInfo
import jp.vpopov.ghwarriors.core.logging.Logging
import jp.vpopov.ghwarriors.core.logging.e
import jp.vpopov.ghwarriors.core.logging.withTagLazy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BookmarkState(
    val bookmarks: List<UserInfo> = emptyList()
)

class BookmarkViewModel @AssistedInject constructor(
    private val bookmarkRepository: BookmarkRepository,
    dispatchers: AppDispatchers
) : DecomposeViewModel(dispatchers) {
    private val logger by Logging.withTagLazy(this::class)
    private val _state = MutableStateFlow(BookmarkState())
    val state: StateFlow<BookmarkState> = _state.asStateFlow()

    init {
        loadBookmarks()
    }

    private fun loadBookmarks() {
        viewModelScope.launch {
            bookmarkRepository.observeUserBookmarks()
                .catch { emit(emptyList()) }
                .collectLatest { bookmarks ->
                    _state.update { it.copy(bookmarks = bookmarks) }
                }
        }
    }

    fun removeBookmark(user: UserInfo) {
        viewModelScope.launch {
            bookmarkRepository.removeUserBookmark(user)
                .onFailure { exception ->
                    logger.e(exception) { "Failed to remove bookmark for user: ${user.userName}" }
                }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(): BookmarkViewModel
    }
}