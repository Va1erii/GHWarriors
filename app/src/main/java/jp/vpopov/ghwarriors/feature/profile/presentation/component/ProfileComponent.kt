package jp.vpopov.ghwarriors.feature.profile.presentation.component

import androidx.paging.PagingData
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import jp.vpopov.ghwarriors.core.domain.model.UserRepoInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

interface ProfileComponent {
    val model: StateFlow<ProfileState>
    val repositories: Flow<PagingData<UserRepoInfo>>

    fun onRepositorySelected(repoInfo: UserRepoInfo)
    fun onUserBookmarkToggle()
    fun onBackButtonPressed()

    interface Factory {
        fun create(
            componentContext: ComponentContext,
            userId: Int,
            onRepositorySelected: (repoInfo: UserRepoInfo) -> Unit,
            onBackPressed: () -> Unit
        ): ProfileComponent
    }
}

class DefaultProfileComponentFactory @Inject constructor(
    private val viewModelFactory: ProfileViewModel.Factory
) : ProfileComponent.Factory {
    override fun create(
        componentContext: ComponentContext,
        userId: Int,
        onRepositorySelected: (repoInfo: UserRepoInfo) -> Unit,
        onBackPressed: () -> Unit
    ): ProfileComponent = DefaultProfileComponent(
        componentContext = componentContext,
        userId = userId,
        repositorySelected = onRepositorySelected,
        onBackPressed = onBackPressed,
        viewModelFactory = viewModelFactory
    )
}

class DefaultProfileComponent(
    private val componentContext: ComponentContext,
    private val userId: Int,
    private val repositorySelected: (repoInfo: UserRepoInfo) -> Unit,
    private val onBackPressed: () -> Unit,
    private val viewModelFactory: ProfileViewModel.Factory
) : ProfileComponent, ComponentContext by componentContext {
    private val viewModel: ProfileViewModel = instanceKeeper.getOrCreate {
        viewModelFactory.create(userId)
    }

    override val model: StateFlow<ProfileState> = viewModel.state
    override val repositories: Flow<PagingData<UserRepoInfo>> = viewModel.repositories
    override fun onRepositorySelected(repoInfo: UserRepoInfo) = repositorySelected(repoInfo)
    override fun onBackButtonPressed() {
        onBackPressed()
    }

    override fun onUserBookmarkToggle() {
    }
}
