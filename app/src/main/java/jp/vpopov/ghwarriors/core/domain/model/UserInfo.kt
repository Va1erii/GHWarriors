package jp.vpopov.ghwarriors.core.domain.model

/**
 * Basic GitHub user information.
 */
data class UserInfo(
    /** User's unique identifier */
    val id: Int,
    /** User's GitHub username */
    val userName: String,
    /** URL to user's avatar image */
    val avatarUrl: String,
)