package jp.vpopov.ghwarriors.core.domain.model

data class UserProfileInfo(
    val userId: Int,
    val name: String?,
    val userName: String,
    val avatarUrl: String,
    val bio: String?,
    val followersCount: Int,
    val followingCount: Int,
    val publicReposCount: Int,
)