package jp.vpopov.ghwarriors.feature.profile.presentation.component

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import jp.vpopov.ghwarriors.core.data.repository.UserRepository
import jp.vpopov.ghwarriors.core.decompose.DecomposeViewModel
import jp.vpopov.ghwarriors.core.domain.model.UserProfileInfo
import jp.vpopov.ghwarriors.core.domain.model.UserRepositoryInfo
import jp.vpopov.ghwarriors.core.logging.Logging
import jp.vpopov.ghwarriors.feature.profile.data.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileState(
    val userId: Int,
    val isLoading: Boolean = true,
    val userProfileInfo: UserProfileInfo? = null,
    val repositories: List<UserRepositoryInfo> = emptyList()
)

class ProfileViewModel @AssistedInject constructor(
    @Assisted private val userId: Int,
    private val profileRepository: ProfileRepository,
    private val userRepository: UserRepository,
) : DecomposeViewModel() {
    private val _state = MutableStateFlow(ProfileState(userId))
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            profileRepository.fetchUserProfile(userId)
                .onSuccess { userProfileInfo ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            userProfileInfo = userProfileInfo
                        )
                    }
                }
                .onFailure {
                    Logging.e(it) { "Fetch user profile error" }
                }
        }
        viewModelScope.launch {
            userRepository.fetchPublicRepositories(userId)
                .onSuccess { repositories ->
                    _state.update { it.copy(repositories = repositories) }
                }
                .onFailure {
                    Logging.e(it) { "Fetch user repositories error" }
                }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(userId: Int): ProfileViewModel
    }
}