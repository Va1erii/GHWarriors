package jp.vpopov.ghwarriors.core.data.userrepo

import jp.vpopov.ghwarriors.core.data.userrepo.dto.UserRepoInfoDTO
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface UserRepoApi {
    @GET("user/{userId}/repos")
    suspend fun fetchUserRepositories(
        @Path("userId") userId: Int,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): List<UserRepoInfoDTO>
}