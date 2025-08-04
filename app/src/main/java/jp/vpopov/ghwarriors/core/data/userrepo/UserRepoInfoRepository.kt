package jp.vpopov.ghwarriors.core.data.userrepo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.map
import jp.vpopov.ghwarriors.core.data.userrepo.datasource.UserRepoPagingSource
import jp.vpopov.ghwarriors.core.data.userrepo.dto.UserRepoInfoDTO
import jp.vpopov.ghwarriors.core.data.userrepo.dto.asDomainModel
import jp.vpopov.ghwarriors.core.domain.model.UserRepoInfo
import jp.vpopov.ghwarriors.core.logging.Logging
import jp.vpopov.ghwarriors.core.logging.d
import jp.vpopov.ghwarriors.core.logging.withTagLazy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import javax.inject.Inject

interface UserRepoInfoRepository {
    fun fetchRepositories(userId: Int): Flow<PagingData<UserRepoInfo>>
    fun refreshRepositories()
}

class UserRepoInfoRepositoryImpl @Inject constructor(
    private val userRepoApi: UserRepoApi
) : UserRepoInfoRepository {
    private val logger by Logging.withTagLazy(this::class)
    private var repoPagingSource: PagingSource<Int, UserRepoInfoDTO>? = null

    override fun fetchRepositories(userId: Int): Flow<PagingData<UserRepoInfo>> {
        logger.d { "Fetch user repositories, userId=($userId)" }
        repoPagingSource?.invalidate()
        return Pager(
            config = PagingConfig(
                pageSize = UserRepoPagingSource.PER_PAGE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                UserRepoPagingSource(
                    userId = userId,
                    userRepoApi = userRepoApi
                ).also { repoPagingSource = it }
            }
        )
            .flow
            .map { pagingData -> pagingData.map { it.asDomainModel() } }
            .onCompletion { repoPagingSource = null }
    }

    override fun refreshRepositories() {
        logger.d { "Refresh user repositories" }
        repoPagingSource?.invalidate()
    }
}