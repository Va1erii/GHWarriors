package jp.vpopov.ghwarriors.core.data.repository.dto

import jp.vpopov.ghwarriors.core.domain.model.UserRepositoryInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserRepositoryDTO(
    val id: Int,
    val name: String,
    @SerialName("full_name") val fullName: String,
    val description: String?,
    val url: String,
    val language: String?,
    @SerialName("stargazers_count") val starsCount: Int
)

fun UserRepositoryDTO.asDomainModel() = UserRepositoryInfo(
    id = id,
    name = name,
    fullName = fullName,
    description = description,
    url = url,
    language = language,
    starsCount = starsCount
)