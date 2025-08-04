package jp.vpopov.ghwarriors.feature.search.presentation.component

import androidx.paging.LoadState
import androidx.paging.LoadState.NotLoading
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import jp.vpopov.ghwarriors.core.data.bookmark.BookmarkRepository
import jp.vpopov.ghwarriors.core.data.search.SearchRepository
import jp.vpopov.ghwarriors.core.decompose.DecomposeViewModel
import jp.vpopov.ghwarriors.core.dispatchers.AppDispatchers
import jp.vpopov.ghwarriors.feature.search.presentation.component.SearchComponent.UserWithBookmarkInfo
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SearchState(
    val query: String = "",
    val users: PagingData<UserWithBookmarkInfo> = PagingData.empty(
        sourceLoadStates = LoadStates(
            refresh = NotLoading(false),
            prepend = NotLoading(false),
            append = NotLoading(false)
        )
    )
)

class SearchViewModel @AssistedInject constructor(
    @Assisted private val query: String?,
    private val searchRepository: SearchRepository,
    private val bookmarkRepository: BookmarkRepository,
    dispatchers: AppDispatchers
) : DecomposeViewModel(dispatchers) {
    private val bookmarkedUserIds = MutableStateFlow(emptySet<Int>())
    private val _state = MutableStateFlow(SearchState(query = query ?: ""))
    private var searchJob: Job? = null
    val state: StateFlow<SearchState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            bookmarkRepository.observeUserBookmarks()
                .catch { emit(emptyList()) }
                .collectLatest { bookmarks ->
                    bookmarkedUserIds.update { bookmarks.map { it.id }.toSet() }
                }
        }
    }

    fun search(query: String) {
        if (_state.value.query == query) return
        when {
            query.isBlank() -> {
                _state.update {
                    it.copy(
                        query = query, users = PagingData.empty(
                            sourceLoadStates = LoadStates(
                                refresh = NotLoading(false),
                                prepend = NotLoading(false),
                                append = NotLoading(false)
                            )
                        )
                    )
                }
            }

            else -> {
                _state.update {
                    it.copy(
                        query = query,
                        users = PagingData.empty(
                            sourceLoadStates = LoadStates(
                                refresh = LoadState.Loading,
                                prepend = NotLoading(false),
                                append = NotLoading(false)
                            )
                        )
                    )
                }
                searchJob?.cancel()
                searchJob = viewModelScope.launch {
                    searchRepository.searchUsers(query)
                        .cachedIn(viewModelScope)
                        .combine(bookmarkedUserIds) { pagingData, bookmarkedUserIds ->
                            pagingData.map { user ->
                                UserWithBookmarkInfo(
                                    user = user,
                                    isBookmarked = bookmarkedUserIds.contains(user.id)
                                )
                            }
                        }
                        .collectLatest { pagingData ->
                            _state.update { it.copy(users = pagingData) }
                        }
                }
            }
        }
    }

    fun bookmarkUser(item: UserWithBookmarkInfo) {
        viewModelScope.launch {
            runCatching {
                if (item.isBookmarked) {
                    bookmarkRepository.removeUserBookmark(item.user)
                } else {
                    bookmarkRepository.addUserBookmark(item.user)
                }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(query: String? = null): SearchViewModel
    }
}