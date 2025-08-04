package jp.vpopov.ghwarriors.core.data.search.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import jp.vpopov.ghwarriors.core.data.search.dto.SearchResponseDTO
import jp.vpopov.ghwarriors.core.data.search.dto.SearchUserDTO
import jp.vpopov.ghwarriors.core.dispatchers.AppDispatchers
import jp.vpopov.ghwarriors.core.logging.Logging
import jp.vpopov.ghwarriors.core.logging.e
import jp.vpopov.ghwarriors.core.logging.withTagLazy
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okio.IOException

/**
 * Defines different mock scenarios for testing pagination behavior.
 */
enum class Scenario {
    /** Returns empty results */
    NO_DATA,

    /** Returns paginated mock user data */
    SUCCESS,

    /** Throws IOException to simulate network issues */
    NO_CONNECTION_ERROR,

    /** Throws generic Exception for server errors */
    SERVER_ERROR,

    /** Succeeds for first 2 pages, then throws error */
    LOAD_MORE_ERROR,
}

/**
 * Mock implementation of [PagingSource] for testing search functionality.
 *
 * Simulates different network scenarios and provides paginated mock user data
 * without requiring actual network calls.
 *
 * @param scenario Controls the mock behavior (defaults to [Scenario.SUCCESS])
 * @param networkDelay Simulates network latency in milliseconds (defaults to 2000ms)
 * @param dispatchers Provides coroutine dispatchers for background operations
 */
class MockSearchPagingSource(
    private val scenario: Scenario = Scenario.SUCCESS,
    private val networkDelay: Long = 2000,
    private val dispatchers: AppDispatchers,
) : PagingSource<Int, SearchUserDTO>() {
    private companion object {
        const val PER_PAGE = 30
    }

    private val logger by Logging.withTagLazy(MockSearchPagingSource::class)

    override fun getRefreshKey(state: PagingState<Int, SearchUserDTO>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SearchUserDTO> {
        return withContext(dispatchers.io) {
            try {
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
                logger.e(exception) { "Error loading data" }
                LoadResult.Error(exception)
            }
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