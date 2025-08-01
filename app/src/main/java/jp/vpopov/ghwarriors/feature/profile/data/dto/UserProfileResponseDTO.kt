package jp.vpopov.ghwarriors.feature.profile.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserProfileResponseDTO(
    val id: Int,
    val login: String,
    @SerialName("avatar_url") val avatarUrl: String,
    val name: String? = null,
    val bio: String? = null,
    val followers: Int,
    val following: Int,
    @SerialName("public_repos") val publicReposCount: Int
)