package jp.vpopov.ghwarriors.core.data.search.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import jp.vpopov.ghwarriors.core.data.search.SearchApi
import jp.vpopov.ghwarriors.core.data.search.dto.SearchUserDTO
import jp.vpopov.ghwarriors.core.logging.Logging
import jp.vpopov.ghwarriors.core.logging.e
import jp.vpopov.ghwarriors.core.logging.withTagLazy
import kotlinx.coroutines.CancellationException

class SearchPagingSource(
    private val searchApi: SearchApi,
    private val query: String
) : PagingSource<Int, SearchUserDTO>() {
    companion object {
        private const val STARTING_PAGE = 1
        const val PER_PAGE = 30
    }

    private val logger by Logging.withTagLazy(this::class)

    override fun getRefreshKey(state: PagingState<Int, SearchUserDTO>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SearchUserDTO> {
        return try {
            val page = params.key ?: STARTING_PAGE
            val response = searchApi.searchUsers(
                query = query,
                page = page,
                perPage = PER_PAGE
            )
            val users = response.items
            val nextKey = if (users.isEmpty()) null else page + 1
            val prevKey = if (page == STARTING_PAGE) null else page - 1
            LoadResult.Page(
                data = users,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            logger.e(exception) { "Error fetching data" }
            LoadResult.Error(exception)
        }
    }
}