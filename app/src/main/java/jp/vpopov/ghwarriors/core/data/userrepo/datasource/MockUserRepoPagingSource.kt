package jp.vpopov.ghwarriors.core.data.userrepo.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import jp.vpopov.ghwarriors.core.data.search.datasource.Scenario
import jp.vpopov.ghwarriors.core.data.userrepo.dto.UserRepoInfoDTO
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
enum class UserRepoScenario {
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
 * Simulates different network scenarios and provides paginated mock user repo data
 * without requiring actual network calls.
 *
 * @param scenario Controls the mock behavior (defaults to [Scenario.SUCCESS])
 * @param networkDelay Simulates network latency in milliseconds (defaults to 2000ms)
 * @param dispatchers Provides coroutine dispatchers for background operations
 */
class MockUserRepoPagingSource(
    private val scenario: UserRepoScenario = UserRepoScenario.SUCCESS,
    private val networkDelay: Long = 2000,
    private val dispatchers: AppDispatchers,
) : PagingSource<Int, UserRepoInfoDTO>() {
    private companion object {
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
        return withContext(dispatchers.io) {
            try {
                delay(networkDelay)
                val page = params.key ?: 1
                val repositories = when (scenario) {
                    UserRepoScenario.NO_DATA -> emptyList()
                    UserRepoScenario.SUCCESS -> createSuccessPageResponse(page)
                    UserRepoScenario.NO_CONNECTION_ERROR -> throw IOException("No connection")
                    UserRepoScenario.SERVER_ERROR -> throw Exception("Load error")
                    UserRepoScenario.LOAD_MORE_ERROR -> if (page > 2) {
                        throw Exception("Load more error")
                    } else {
                        createSuccessPageResponse(page)
                    }
                }
                val nextKey = if (repositories.isEmpty()) null else page + 1
                val prevKey = if (page == 1) null else page - 1
                LoadResult.Page(
                    data = repositories,
                    prevKey = prevKey,
                    nextKey = nextKey
                )
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                logger.e(exception) { "Error loading user repositories" }
                LoadResult.Error(exception)
            }
        }
    }

    private fun createSuccessPageResponse(page: Int): List<UserRepoInfoDTO> {
        val languages = listOf(
            "Kotlin",
            "Java",
            "JavaScript",
            "Python",
            "TypeScript",
            "Swift",
            "Go",
            "Rust",
            "C++",
            "Dart"
        )
        val repoTypes = listOf(
            "library",
            "app",
            "framework",
            "tool",
            "demo",
            "sample",
            "api",
            "client",
            "server",
            "plugin"
        )
        val descriptions = listOf(
            "A modern Android application built with Jetpack Compose and clean architecture principles",
            "High-performance library for efficient data processing and analysis",
            "RESTful API service built with modern frameworks and best practices",
            "Cross-platform mobile application with native performance",
            "Machine learning toolkit for data scientists and developers",
            "Developer tools and utilities for enhanced productivity",
            "Web application framework with built-in security features",
            "Database management system with advanced querying capabilities",
            "Networking library with automatic retry and caching mechanisms",
            "UI component library with customizable themes and animations"
        )

        return List(PER_PAGE) { index ->
            val repoIndex = (page - 1) * PER_PAGE + index + 1
            val language = languages[repoIndex % languages.size]
            val repoType = repoTypes[repoIndex % repoTypes.size]
            val description = descriptions[repoIndex % descriptions.size]

            UserRepoInfoDTO(
                id = repoIndex,
                name = "${language.lowercase()}-$repoType-$repoIndex",
                fullName = "user123/${language.lowercase()}-$repoType-$repoIndex",
                description = if (repoIndex % 7 == 0) null else description,
                url = "https://github.com/user123/${language.lowercase()}-$repoType-$repoIndex",
                language = if (repoIndex % 8 == 0) null else language,
                starsCount = when {
                    repoIndex % 50 == 0 -> (10000..50000).random()
                    repoIndex % 20 == 0 -> (1000..9999).random()
                    repoIndex % 10 == 0 -> (100..999).random()
                    else -> (0..99).random()
                },
                fork = repoIndex % 15 == 0
            )
        }
    }
}