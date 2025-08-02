package jp.vpopov.ghwarriors.core.data.search.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import jp.vpopov.ghwarriors.core.data.search.dto.SearchResponseDTO
import jp.vpopov.ghwarriors.core.data.search.dto.SearchUserDTO
import jp.vpopov.ghwarriors.core.logging.Logging
import kotlinx.coroutines.CancellationException

class MockSearchPagingSource : PagingSource<Int, SearchUserDTO>() {
    override fun getRefreshKey(state: PagingState<Int, SearchUserDTO>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SearchUserDTO> {
        return try {
            val page = params.key ?: 1
            val response = SearchResponseDTO(
                items = List(30) { index ->
                    SearchUserDTO(
                        id = (page - 1) * 30 + index + 1,
                        login = "user${(page - 1) * 30 + index + 1}",
                        avatarUrl = "https://avatars.githubusercontent.com/u/30325285?v=4"
                    )
                }
            )
            val users = response.items
            val nextKey = if (users.isEmpty()) null else page + 1
            val prevKey = if (page == 1) null else page - 1
            LoadResult.Page(
                data = users,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            Logging.e(exception) { "Error loading data" }
            LoadResult.Error(exception)
        }
    }
}