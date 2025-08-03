package jp.vpopov.ghwarriors.feature.profile.presentation.component

import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import jp.vpopov.ghwarriors.core.data.user.UserRepository
import jp.vpopov.ghwarriors.core.data.userrepo.UserRepoInfoRepository
import jp.vpopov.ghwarriors.core.decompose.DecomposeViewModel
import jp.vpopov.ghwarriors.core.domain.model.UserProfileInfo
import jp.vpopov.ghwarriors.core.domain.model.UserRepoInfo
import jp.vpopov.ghwarriors.core.error.AppError
import jp.vpopov.ghwarriors.core.error.ErrorMapper
import jp.vpopov.ghwarriors.core.logging.Logging
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class ProfileState {
    abstract val userId: Int

    data class Loading(
        override val userId: Int
    ) : ProfileState()

    data class Error(
        override val userId: Int,
        val error: AppError
    ) : ProfileState()

    data class Success(
        override val userId: Int,
        val userProfileInfo: UserProfileInfo,
    ) : ProfileState()
}

class ProfileViewModel @AssistedInject constructor(
    @Assisted private val userId: Int,
    private val userRepository: UserRepository,
    private val userRepoInfoRepository: UserRepoInfoRepository,
) : DecomposeViewModel() {
    private val _state = MutableStateFlow<ProfileState>(ProfileState.Loading(userId))
    val state = _state.asStateFlow()
    val repositories: Flow<PagingData<UserRepoInfo>> = userRepoInfoRepository
        .fetchRepositories(userId)
        .cachedIn(viewModelScope)

    init {
        refreshProfile()
    }

    private fun refreshProfile() {
        viewModelScope.launch {
            userRepository.fetchProfile(userId)
                .onSuccess { userProfileInfo ->
                    _state.update {
                        ProfileState.Success(
                            userId = userId,
                            userProfileInfo = userProfileInfo,
                        )
                    }
                }
                .onFailure { throwable ->
                    Logging.e(throwable) { "Fetch user profile error" }
                    _state.update {
                        ProfileState.Error(
                            userId = userId,
                            error = ErrorMapper.convert(throwable)
                        )
                    }
                }
        }
    }

    fun refresh() {
        refreshProfile()
        userRepoInfoRepository.refreshRepositories()
    }

    @AssistedFactory
    interface Factory {
        fun create(userId: Int): ProfileViewModel
    }
}