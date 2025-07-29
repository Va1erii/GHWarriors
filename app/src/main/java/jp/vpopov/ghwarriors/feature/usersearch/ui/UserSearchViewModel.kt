package jp.vpopov.ghwarriors.feature.usersearch.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.vpopov.ghwarriors.feature.usersearch.data.SearchRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class UserSearchViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val searchRepository: SearchRepository
) : ViewModel() {
    val query = savedStateHandle.getStateFlow<String>("query", "tom")
    val users = query
        .flatMapLatest { searchRepository.searchUsers(it) }
        .cachedIn(viewModelScope)

    fun setQuery(query: String) {
        savedStateHandle["query"] = query
    }
}