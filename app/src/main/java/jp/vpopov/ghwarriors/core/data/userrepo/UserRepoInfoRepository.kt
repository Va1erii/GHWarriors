package jp.vpopov.ghwarriors.core.data.userrepo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.map
import jp.vpopov.ghwarriors.core.data.userrepo.datasource.MockUserRepoPagingSource
import jp.vpopov.ghwarriors.core.data.userrepo.datasource.UserRepoPagingSource
import jp.vpopov.ghwarriors.core.data.userrepo.datasource.UserRepoScenario
import jp.vpopov.ghwarriors.core.data.userrepo.dto.UserRepoInfoDTO
import jp.vpopov.ghwarriors.core.data.userrepo.dto.asDomainModel
import jp.vpopov.ghwarriors.core.domain.model.UserRepoInfo
import jp.vpopov.ghwarriors.core.logging.Logging
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface UserRepoInfoRepository {
    fun fetchRepositories(userId: Int): Flow<PagingData<UserRepoInfo>>
    fun refreshRepositories()
}

class UserRepoInfoRepositoryImpl @Inject constructor(
    private val userRepoApi: UserRepoApi
) : UserRepoInfoRepository {
    private var repoPagingSource: PagingSource<Int, UserRepoInfoDTO>? = null

    override fun fetchRepositories(userId: Int): Flow<PagingData<UserRepoInfo>> {
        Logging.d { "Fetch user repositories, userId=($userId)" }
        return Pager(
            config = PagingConfig(
                pageSize = UserRepoPagingSource.PER_PAGE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
//                MockUserRepoPagingSource(
//                    scenario = UserRepoScenario.LOAD_MORE_ERROR
//                ).also { repoPagingSource = it }
                UserRepoPagingSource(
                    userId = userId,
                    userRepoApi = userRepoApi
                ).also { repoPagingSource = it }
            }
        )
            .flow
            .map { pagingData -> pagingData.map { it.asDomainModel() } }
    }

    override fun refreshRepositories() {
        repoPagingSource?.invalidate()
    }
}