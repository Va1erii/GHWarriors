package jp.vpopov.ghwarriors.core.data.user

import jp.vpopov.ghwarriors.core.data.user.dto.asDomainModel
import jp.vpopov.ghwarriors.core.domain.model.UserProfileInfo
import jp.vpopov.ghwarriors.core.extension.throwOnCancellation
import jp.vpopov.ghwarriors.core.logging.Logging
import javax.inject.Inject

interface UserRepository {
    suspend fun fetchProfile(userId: Int): Result<UserProfileInfo>
}

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi
) : UserRepository {
    override suspend fun fetchProfile(userId: Int): Result<UserProfileInfo> {
        return runCatching { userApi.fetchUserProfile(userId) }
            .throwOnCancellation()
            .map { it.asDomainModel() }
            .onFailure { Logging.e(it) { "Failed to fetch user profile" } }
    }
}