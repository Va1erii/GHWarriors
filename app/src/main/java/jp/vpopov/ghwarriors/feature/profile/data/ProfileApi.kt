package jp.vpopov.ghwarriors.feature.profile.data

import jp.vpopov.ghwarriors.feature.profile.data.dto.UserProfileResponseDTO
import retrofit2.http.GET
import retrofit2.http.Path

interface ProfileApi {
    @GET("user/{account_id}")
    suspend fun fetchUserProfile(
        @Path("account_id") userId: Int
    ): UserProfileResponseDTO
}