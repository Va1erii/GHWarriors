package jp.vpopov.ghwarriors.core.data.user

import jp.vpopov.ghwarriors.core.data.user.dto.asDomainModel
import jp.vpopov.ghwarriors.core.domain.model.UserProfileInfo
import jp.vpopov.ghwarriors.core.extension.throwOnCancellation
import jp.vpopov.ghwarriors.core.logging.Logging
import kotlinx.coroutines.delay
import okio.IOException
import javax.inject.Inject

interface UserRepository {
    suspend fun fetchProfile(userId: Int): Result<UserProfileInfo>
}

class MockUserRepository @Inject constructor() : UserRepository {
    override suspend fun fetchProfile(userId: Int): Result<UserProfileInfo> {
        delay(2000)
//        return Result.failure(IOException("Failed to fetch user profile"))

//        return Result.failure(Exception("Failed to fetch user profile"))

        return Result.success(
            UserProfileInfo(
                userId = userId,
                name = "John Doe",
                userName = "john_doe",
                avatarUrl = "https://avatars.githubusercontent.com/u/30325285?v=4",
                bio = "This is a sample bio for John Doe.",
                followersCount = 150,
                followingCount = 75,
                publicReposCount = 10
            )
        )
    }
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