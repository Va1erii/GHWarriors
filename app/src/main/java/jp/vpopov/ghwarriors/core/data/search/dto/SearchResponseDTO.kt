package jp.vpopov.ghwarriors.core.data.search.dto

import jp.vpopov.ghwarriors.core.domain.model.UserInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchResponseDTO(
    val items: List<SearchUserDTO>
)

@Serializable
data class SearchUserDTO(
    val id: Int,
    val login: String,
    @SerialName("avatar_url") val avatarUrl: String,
)

fun SearchUserDTO.asDomainModel(): UserInfo {
    return UserInfo(
        id = id,
        userName = login,
        avatarUrl = avatarUrl
    )
}