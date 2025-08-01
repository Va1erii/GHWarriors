package jp.vpopov.ghwarriors.core.data.repository

import jp.vpopov.ghwarriors.core.data.repository.dto.UserRepositoryDTO
import retrofit2.http.GET
import retrofit2.http.Path

interface RepositoryApi {
    @GET("user/{userId}/repos")
    suspend fun fetchUserRepositories(
        @Path("userId") userId: Int
    ): List<UserRepositoryDTO>
}