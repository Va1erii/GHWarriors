package jp.vpopov.ghwarriors.core.data.user.dto

import jp.vpopov.ghwarriors.core.domain.model.UserProfileInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserProfileInfoDTO(
    val id: Int,
    val login: String,
    @SerialName("avatar_url") val avatarUrl: String,
    val name: String? = null,
    val bio: String? = null,
    val followers: Int,
    val following: Int,
    @SerialName("public_repos") val publicReposCount: Int
)

fun UserProfileInfoDTO.asDomainModel(): UserProfileInfo = UserProfileInfo(
    userId = id,
    name = name,
    userName = login,
    avatarUrl = avatarUrl,
    bio = bio,
    followersCount = followers,
    followingCount = following,
    publicReposCount = publicReposCount
)