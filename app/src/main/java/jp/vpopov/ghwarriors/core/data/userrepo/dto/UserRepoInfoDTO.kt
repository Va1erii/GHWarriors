package jp.vpopov.ghwarriors.core.data.userrepo.dto

import jp.vpopov.ghwarriors.core.domain.model.UserRepoInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserRepoInfoDTO(
    val id: Int,
    val name: String,
    @SerialName("full_name") val fullName: String,
    val description: String?,
    @SerialName("html_url") val url: String,
    val language: String?,
    val fork: Boolean,
    @SerialName("stargazers_count") val starsCount: Int,
)

fun UserRepoInfoDTO.asDomainModel() = UserRepoInfo(
    id = id,
    name = name,
    fullName = fullName,
    description = description,
    url = url,
    language = language,
    starsCount = starsCount,
    isFork = fork
)