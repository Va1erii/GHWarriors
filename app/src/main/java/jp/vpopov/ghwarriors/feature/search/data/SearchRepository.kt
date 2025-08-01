package jp.vpopov.ghwarriors.feature.search.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import jp.vpopov.ghwarriors.core.domain.model.UserInfo
import jp.vpopov.ghwarriors.core.logging.Logging
import jp.vpopov.ghwarriors.feature.search.data.datasource.SearchPagingSource
import jp.vpopov.ghwarriors.feature.search.data.dto.asDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface SearchRepository {
    suspend fun searchUsers(
        query: String,
    ): Flow<PagingData<UserInfo>>
}

class SearchRepositoryImpl @Inject constructor(
    private val searchApi: SearchApi,
) : SearchRepository {
    override suspend fun searchUsers(query: String): Flow<PagingData<UserInfo>> {
        Logging.d { "Search users, query=($query)" }
        return Pager(
            config = PagingConfig(
                pageSize = SearchPagingSource.PER_PAGE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
//                MockSearchPagingSource()
                SearchPagingSource(
                    searchApi = searchApi,
                    query = query
                )
            }
        )
            .flow
            .map { pagingData -> pagingData.map { it.asDomainModel() } }
    }
}