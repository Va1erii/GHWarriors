package jp.vpopov.ghwarriors.feature.usersearch.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import jp.vpopov.ghwarriors.core.domain.model.User
import jp.vpopov.ghwarriors.feature.usersearch.data.datasource.MockUserSearchPagingSource
import jp.vpopov.ghwarriors.feature.usersearch.data.datasource.UserSearchPagingSource
import jp.vpopov.ghwarriors.feature.usersearch.data.dto.asDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface SearchRepository {
    suspend fun searchUsers(
        query: String,
    ): Flow<PagingData<User>>
}

class SearchRepositoryImpl @Inject constructor(
    private val userSearchApi: UserSearchApi
) : SearchRepository {
    override suspend fun searchUsers(query: String): Flow<PagingData<User>> {
        return Pager(
            config = PagingConfig(
                pageSize = UserSearchPagingSource.PER_PAGE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                MockUserSearchPagingSource()
//                SearchPagingSource(
//                    searchApi = searchApi,
//                    query = query
//                )
            }
        )
            .flow
            .map { pagingData -> pagingData.map { it.asDomainModel() } }
    }
}