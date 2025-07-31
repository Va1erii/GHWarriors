package jp.vpopov.ghwarriors.feature.search.presentation.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import jp.vpopov.ghwarriors.core.domain.model.UserInfo
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.builtins.serializer
import javax.inject.Inject

interface SearchComponent {
    val model: StateFlow<SearchState>

    fun search(query: String)
    fun onUserSelected(user: UserInfo)

    interface Factory {
        fun create(
            componentContext: ComponentContext,
            userSelected: (UserInfo) -> Unit
        ): SearchComponent
    }
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
        viewModelFactory.create(stateKeeper.consume<String>(STATE_KEY, String.serializer()))
    }

    override val model: StateFlow<SearchState> = viewModel.state

    init {
        stateKeeper.register(STATE_KEY, String.serializer()) { viewModel.state.value.query }
    }

    override fun search(query: String) {
        viewModel.search(query)
    }

    override fun onUserSelected(user: UserInfo) = userSelected(user)

    private companion object {
        const val STATE_KEY = "search_state"
    }
}