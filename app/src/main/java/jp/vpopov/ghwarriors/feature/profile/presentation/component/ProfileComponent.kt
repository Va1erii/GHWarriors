package jp.vpopov.ghwarriors.feature.profile.presentation.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

interface ProfileComponent {
    val model: StateFlow<ProfileState>

    fun onRepositorySelected()
    fun onUserBookmarkToggle()
    fun onBackButtonPressed()

    interface Factory {
        fun create(
            componentContext: ComponentContext,
            userId: Int,
            onRepositorySelected: () -> Unit,
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
        onRepositorySelected: () -> Unit,
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
    private val repositorySelected: () -> Unit,
    private val onBackPressed: () -> Unit,
    private val viewModelFactory: ProfileViewModel.Factory
) : ProfileComponent, ComponentContext by componentContext {
    private val viewModel: ProfileViewModel = instanceKeeper.getOrCreate {
        viewModelFactory.create(userId)
    }

    override val model: StateFlow<ProfileState> = viewModel.state
    override fun onRepositorySelected() = repositorySelected()
    override fun onBackButtonPressed() {
        onBackPressed()
    }
    override fun onUserBookmarkToggle() {
    }
}
