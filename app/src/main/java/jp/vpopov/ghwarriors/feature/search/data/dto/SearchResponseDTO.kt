package jp.vpopov.ghwarriors.feature.search.data.dto

import jp.vpopov.ghwarriors.core.domain.model.User
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

fun SearchUserDTO.asDomainModel(): User {
    return User(
        id = id,
        userName = login,
        avatarUrl = avatarUrl
    )
}