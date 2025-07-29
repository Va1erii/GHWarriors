package jp.vpopov.ghwarriors.feature.usersearch.data

import jp.vpopov.ghwarriors.feature.usersearch.data.dto.UserSearchResponseDTO
import retrofit2.http.GET
import retrofit2.http.Query

interface UserSearchApi {
    @GET("search/users")
    suspend fun searchUsers(
        @Query("q") query: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 30
    ): UserSearchResponseDTO
}