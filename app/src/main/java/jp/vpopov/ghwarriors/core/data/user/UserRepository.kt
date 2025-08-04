package jp.vpopov.ghwarriors.core.data.user

import jp.vpopov.ghwarriors.core.data.user.dto.asDomainModel
import jp.vpopov.ghwarriors.core.domain.model.UserProfileInfo
import jp.vpopov.ghwarriors.core.extension.throwOnCancellation
import jp.vpopov.ghwarriors.core.logging.Logging
import jp.vpopov.ghwarriors.core.logging.e
import jp.vpopov.ghwarriors.core.logging.withTagLazy
import javax.inject.Inject

interface UserRepository {
    suspend fun fetchProfile(userId: Int): Result<UserProfileInfo>
}

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi
) : UserRepository {
    private val logger by Logging.withTagLazy(this::class)

    override suspend fun fetchProfile(userId: Int): Result<UserProfileInfo> {
        return runCatching { userApi.fetchUserProfile(userId) }
            .throwOnCancellation()
            .map { it.asDomainModel() }
            .onFailure { logger.e(it) { "Failed to fetch user profile" } }
    }
}