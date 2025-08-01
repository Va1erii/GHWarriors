package jp.vpopov.ghwarriors.feature.profile.data

import jp.vpopov.ghwarriors.core.domain.model.UserProfileInfo
import jp.vpopov.ghwarriors.core.logging.Logging
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

interface ProfileRepository {
    suspend fun fetchUserProfile(userId: Int): Result<UserProfileInfo>
}

class MockProfileRepository @Inject constructor() : ProfileRepository {
    override suspend fun fetchUserProfile(userId: Int): Result<UserProfileInfo> {
        return Result.success(
            UserProfileInfo(
                userId = 1,
                name = "John Snow",
                userName = "John_Snow",
                avatarUrl = "",
                bio = "I don't know",
                followersCount = 1,
                followingCount = 1000,
                totalPublicReposCount = 10
            )
        )
    }
}

class ProfileRepositoryImpl @Inject constructor(
    private val profileApi: ProfileApi
) : ProfileRepository {
    override suspend fun fetchUserProfile(userId: Int): Result<UserProfileInfo> {
        return try {
            val response = profileApi.fetchUserProfile(userId)
            Result.success(
                UserProfileInfo(
                    userId = response.id,
                    name = response.name,
                    userName = response.login,
                    avatarUrl = response.avatarUrl,
                    bio = response.bio,
                    followersCount = response.followers,
                    followingCount = response.following,
                    totalPublicReposCount = response.publicReposCount
                )
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Logging.e(e) { "Failed to fetch user profile" }
            println(e)
            Result.failure(e)
        }
    }
}