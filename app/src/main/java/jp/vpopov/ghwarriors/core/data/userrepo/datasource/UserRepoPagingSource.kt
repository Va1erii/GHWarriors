package jp.vpopov.ghwarriors.core.data.userrepo.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import jp.vpopov.ghwarriors.core.data.userrepo.UserRepoApi
import jp.vpopov.ghwarriors.core.data.userrepo.dto.UserRepoInfoDTO
import jp.vpopov.ghwarriors.core.logging.Logging
import jp.vpopov.ghwarriors.core.logging.e
import jp.vpopov.ghwarriors.core.logging.withTagLazy
import kotlinx.coroutines.CancellationException

class UserRepoPagingSource(
    private val userRepoApi: UserRepoApi,
    private val userId: Int
) : PagingSource<Int, UserRepoInfoDTO>() {
    companion object {
        private const val STARTING_PAGE = 1
        const val PER_PAGE = 30
    }

    private val logger by Logging.withTagLazy(this::class)

    override fun getRefreshKey(state: PagingState<Int, UserRepoInfoDTO>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserRepoInfoDTO> {
        return try {
            val page = params.key ?: STARTING_PAGE
            val repositories = userRepoApi.fetchUserRepositories(
                userId = userId,
                page = page,
                perPage = PER_PAGE
            )
            val nextKey = if (repositories.isEmpty()) null else page + 1
            val prevKey = if (page == STARTING_PAGE) null else page - 1
            LoadResult.Page(
                data = repositories,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            logger.e(exception) { "Error loading data" }
            LoadResult.Error(exception)
        }
    }
}