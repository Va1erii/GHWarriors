package jp.vpopov.ghwarriors.core.data.userrepo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import jp.vpopov.ghwarriors.core.data.userrepo.datasource.UserRepoPagingSource
import jp.vpopov.ghwarriors.core.data.userrepo.dto.asDomainModel
import jp.vpopov.ghwarriors.core.domain.model.UserRepoInfo
import jp.vpopov.ghwarriors.core.logging.Logging
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface UserRepoInfoRepository {
    fun fetchPublicRepositories(userId: Int): Flow<PagingData<UserRepoInfo>>
}

class UserRepoInfoRepositoryImpl @Inject constructor(
    private val userRepoApi: UserRepoApi
) : UserRepoInfoRepository {

    override fun fetchPublicRepositories(userId: Int): Flow<PagingData<UserRepoInfo>> {
        Logging.d { "Fetch user public repositories, userId=($userId)" }
        return Pager(
            config = PagingConfig(
                pageSize = UserRepoPagingSource.PER_PAGE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                UserRepoPagingSource(
                    userId = userId,
                    userRepoApi = userRepoApi
                )
            }
        )
            .flow
            .map { pagingData -> pagingData.map { it.asDomainModel() } }
    }
}