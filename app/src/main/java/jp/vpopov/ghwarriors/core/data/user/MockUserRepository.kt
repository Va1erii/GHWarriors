package jp.vpopov.ghwarriors.core.data.user

import jp.vpopov.ghwarriors.core.dispatchers.AppDispatchers
import jp.vpopov.ghwarriors.core.domain.model.UserProfileInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MockUserRepository @Inject constructor(
    private val dispatchers: AppDispatchers
) : UserRepository {
    override suspend fun fetchProfile(userId: Int): Result<UserProfileInfo> {
        return withContext(dispatchers.io) {
            delay(2000)
            Result.success(
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
}