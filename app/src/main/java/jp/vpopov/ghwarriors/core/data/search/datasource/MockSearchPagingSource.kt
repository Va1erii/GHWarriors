package jp.vpopov.ghwarriors.core.data.search.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import jp.vpopov.ghwarriors.core.data.search.dto.SearchResponseDTO
import jp.vpopov.ghwarriors.core.data.search.dto.SearchUserDTO
import jp.vpopov.ghwarriors.core.logging.Logging
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import okio.IOException

enum class Scenario {
    NO_DATA,
    SUCCESS,
    NO_CONNECTION_ERROR,
    SERVER_ERROR,
    LOAD_MORE_ERROR,
}

class MockSearchPagingSource(
    private val scenario: Scenario = Scenario.SUCCESS,
    private val networkDelay: Long = 2000,
) : PagingSource<Int, SearchUserDTO>() {
    private companion object {
        const val PER_PAGE = 30
    }

    override fun getRefreshKey(state: PagingState<Int, SearchUserDTO>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SearchUserDTO> {
        return try {
            delay(networkDelay)
            val page = params.key ?: 1
            val response = when (scenario) {
                Scenario.NO_DATA -> SearchResponseDTO(emptyList())
                Scenario.SUCCESS -> createSuccessPageResponse(page)
                Scenario.NO_CONNECTION_ERROR -> throw IOException("No connection")
                Scenario.SERVER_ERROR -> throw Exception("Load error")
                Scenario.LOAD_MORE_ERROR -> if (page > 2) {
                    throw Exception("Load more error")
                } else {
                    createSuccessPageResponse(page)
                }
            }
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

    private fun createSuccessPageResponse(
        page: Int
    ): SearchResponseDTO {
        return SearchResponseDTO(
            items = List(PER_PAGE) { index ->
                SearchUserDTO(
                    id = (page - 1) * 30 + index + 1,
                    login = "user${(page - 1) * 30 + index + 1}",
                    avatarUrl = "https://avatars.githubusercontent.com/u/30325285?v=4"
                )
            }
        )
    }
}