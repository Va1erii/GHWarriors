package jp.vpopov.ghwarriors.core.data.user

import jp.vpopov.ghwarriors.core.data.user.dto.UserProfileInfoDTO
import retrofit2.http.GET
import retrofit2.http.Path

interface UserApi {
    @GET("user/{user_id}")
    suspend fun fetchUserProfile(
        @Path("user_id") userId: Int
    ): UserProfileInfoDTO
}