package jp.vpopov.ghwarriors.feature.bookmark.presentation.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import jp.vpopov.ghwarriors.core.domain.model.UserInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface BookmarkComponent {
    val bookmarks: Flow<List<UserInfo>>

    fun onUserSelected(user: UserInfo)
    fun onRemoveBookmark(user: UserInfo)

    interface Factory {
        fun create(
            componentContext: ComponentContext,
            userSelected: (UserInfo) -> Unit
        ): BookmarkComponent
    }
}

class DefaultBookmarkComponentFactory @Inject constructor(
    private val viewModelFactory: BookmarkViewModel.Factory
) : BookmarkComponent.Factory {
    override fun create(
        componentContext: ComponentContext,
        userSelected: (UserInfo) -> Unit
    ): BookmarkComponent = DefaultBookmarkComponent(
        componentContext = componentContext,
        userSelected = userSelected,
        viewModelFactory = viewModelFactory
    )
}

class DefaultBookmarkComponent(
    componentContext: ComponentContext,
    private val userSelected: (UserInfo) -> Unit,
    private val viewModelFactory: BookmarkViewModel.Factory
) : BookmarkComponent, ComponentContext by componentContext {
    private val viewModel: BookmarkViewModel = instanceKeeper.getOrCreate {
        viewModelFactory.create()
    }

    override val bookmarks: Flow<List<UserInfo>> = viewModel.state.map { it.bookmarks }

    override fun onUserSelected(user: UserInfo) = userSelected(user)

    override fun onRemoveBookmark(user: UserInfo) {
        viewModel.removeBookmark(user)
    }
}