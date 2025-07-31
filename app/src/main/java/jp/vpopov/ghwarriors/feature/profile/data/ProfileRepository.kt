package jp.vpopov.ghwarriors.feature.profile.data

import jp.vpopov.ghwarriors.core.domain.model.UserProfileInfo
import javax.inject.Inject

interface ProfileRepository {
    suspend fun fetchUserProfile(userId: Int): Result<UserProfileInfo>
}

class MockProfileRepository @Inject constructor(): ProfileRepository{
    override suspend fun fetchUserProfile(userId: Int): Result<UserProfileInfo> {
        return Result.success(UserProfileInfo(
            userId = 1,
            name = "John Snow",
            userName = "John_Snow",
            avatarUrl = "",
            bio = "I don't know",
            followersCount = 1,
            followingCount = 1000
        ))
    }
}