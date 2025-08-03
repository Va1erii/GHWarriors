package jp.vpopov.ghwarriors.feature.search.presentation.component

import androidx.paging.PagingData
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import jp.vpopov.ghwarriors.core.domain.model.UserInfo
import jp.vpopov.ghwarriors.feature.search.presentation.component.SearchComponent.UserWithBookmarkInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.builtins.serializer
import javax.inject.Inject

interface SearchComponent {
    val query: Flow<String>
    val users: Flow<PagingData<UserWithBookmarkInfo>>

    fun search(query: String)
    fun onUserSelected(user: UserWithBookmarkInfo)
    fun onBookmarkClick(user: UserWithBookmarkInfo)

    interface Factory {
        fun create(
            componentContext: ComponentContext,
            userSelected: (UserInfo) -> Unit
        ): SearchComponent
    }

    data class UserWithBookmarkInfo(
        val user: UserInfo,
        val isBookmarked: Boolean
    )
}

class DefaultSearchComponentFactory @Inject constructor(
    private val viewModelFactory: SearchViewModel.Factory
) : SearchComponent.Factory {
    override fun create(
        componentContext: ComponentContext,
        userSelected: (UserInfo) -> Unit
    ): SearchComponent = DefaultSearchComponent(
        componentContext = componentContext,
        userSelected = userSelected,
        viewModelFactory = viewModelFactory
    )
}

class DefaultSearchComponent(
    componentContext: ComponentContext,
    private val userSelected: (UserInfo) -> Unit,
    private val viewModelFactory: SearchViewModel.Factory
) : SearchComponent, ComponentContext by componentContext {
    private val viewModel: SearchViewModel = instanceKeeper.getOrCreate {
        viewModelFactory.create(stateKeeper.consume(QUERY_KEY, String.serializer()))
    }

    override val query: Flow<String> = viewModel.state.map { it.query }
    override val users: Flow<PagingData<UserWithBookmarkInfo>> = viewModel.state.map { it.users }

    init {
        stateKeeper.register(QUERY_KEY, String.serializer()) { viewModel.state.value.query }
    }

    override fun search(query: String) {
        viewModel.search(query)
    }

    override fun onUserSelected(user: UserWithBookmarkInfo) = userSelected(user.user)

    override fun onBookmarkClick(user: UserWithBookmarkInfo) {
        viewModel.bookmarkUser(user)
    }

    private companion object {
        const val QUERY_KEY = "search_query"
    }
}