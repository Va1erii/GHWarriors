package jp.vpopov.ghwarriors.core.domain.model

/**
 * Detailed GitHub user profile information.
 */
data class UserProfileInfo(
    /** User's unique identifier */
    val userId: Int,
    /** User's display name (optional) */
    val name: String?,
    /** User's GitHub username */
    val userName: String,
    /** URL to user's avatar image */
    val avatarUrl: String,
    /** User's bio description (optional) */
    val bio: String?,
    /** Number of followers */
    val followersCount: Int,
    /** Number of users being followed */
    val followingCount: Int,
    /** Number of public repositories */
    val publicReposCount: Int,
)