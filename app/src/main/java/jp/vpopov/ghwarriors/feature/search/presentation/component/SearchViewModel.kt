package jp.vpopov.ghwarriors.feature.search.presentation.component

import androidx.paging.LoadState
import androidx.paging.LoadState.NotLoading
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import jp.vpopov.ghwarriors.core.data.search.SearchRepository
import jp.vpopov.ghwarriors.core.decompose.DecomposeViewModel
import jp.vpopov.ghwarriors.core.domain.model.UserInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SearchState(
    val query: String = "",
    val users: PagingData<UserInfo> = PagingData.empty(
        sourceLoadStates = LoadStates(
            refresh = NotLoading(false),
            prepend = NotLoading(false),
            append = NotLoading(false)
        )
    )
)

class SearchViewModel @AssistedInject constructor(
    @Assisted private val query: String?,
    private val searchRepository: SearchRepository
) : DecomposeViewModel() {
    private val _state = MutableStateFlow(SearchState(query = query ?: ""))
    val state: StateFlow<SearchState> = _state.asStateFlow()

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
                viewModelScope.launch {
                    searchRepository.searchUsers(query)
                        .cachedIn(viewModelScope)
                        .collectLatest { pagingData ->
                            _state.update { it.copy(users = pagingData) }
                        }
                }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(query: String? = null): SearchViewModel
    }
}