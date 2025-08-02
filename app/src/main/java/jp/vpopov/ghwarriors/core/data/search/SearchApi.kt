package jp.vpopov.ghwarriors.core.data.search

import jp.vpopov.ghwarriors.core.data.search.dto.SearchResponseDTO
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApi {
    @GET("search/users")
    suspend fun searchUsers(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): SearchResponseDTO
}