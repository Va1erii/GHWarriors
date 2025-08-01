package jp.vpopov.ghwarriors.core.domain.model

data class UserRepositoryInfo(
    val id: Int,
    val name: String,
    val fullName: String,
    val description: String?,
    val url: String,
    val language: String?,
    val starsCount: Int
)