package jp.vpopov.ghwarriors.feature.search.data

import jp.vpopov.ghwarriors.feature.search.data.dto.SearchResponseDTO
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApi {
    @GET("search/users")
    suspend fun searchUsers(
        @Query("q") query: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 30
    ): SearchResponseDTO
}