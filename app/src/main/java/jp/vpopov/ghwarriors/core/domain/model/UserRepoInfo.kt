package jp.vpopov.ghwarriors.core.domain.model

/**
 * GitHub repository information.
 */
data class UserRepoInfo(
    /** Repository's unique identifier */
    val id: Int,
    /** Repository name */
    val name: String,
    /** Full repository name (owner/repo) */
    val fullName: String,
    /** Repository description (optional) */
    val description: String?,
    /** Repository URL */
    val url: String,
    /** Primary programming language (optional) */
    val language: String?,
    /** Number of stars */
    val starsCount: Int,
    /** Whether this is a forked repository */
    val isFork: Boolean,
)