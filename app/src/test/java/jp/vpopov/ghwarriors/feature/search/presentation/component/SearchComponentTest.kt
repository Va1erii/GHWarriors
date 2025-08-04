package jp.vpopov.ghwarriors.feature.search.presentation.component

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.paging.testing.asSnapshot
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jp.vpopov.ghwarriors.core.data.bookmark.BookmarkRepository
import jp.vpopov.ghwarriors.core.data.search.SearchRepository
import jp.vpopov.ghwarriors.core.data.search.datasource.MockSearchPagingSource
import jp.vpopov.ghwarriors.core.data.search.datasource.Scenario
import jp.vpopov.ghwarriors.core.data.search.dto.asDomainModel
import jp.vpopov.ghwarriors.core.dispatchers.AppDispatchers
import jp.vpopov.ghwarriors.core.domain.model.UserInfo
import jp.vpopov.ghwarriors.feature.search.presentation.component.SearchComponent.UserWithBookmarkInfo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchComponentTest {

    private val lifecycle = LifecycleRegistry()
    private lateinit var componentFactory: SearchComponent.Factory
    private lateinit var componentContext: ComponentContext
    private lateinit var searchRepository: SearchRepository
    private lateinit var bookmarkRepository: BookmarkRepository
    private lateinit var testDispatcher: CoroutineDispatcher

    // Test data
    private val testUser1 = UserInfo(
        id = 1,
        userName = "user1",
        avatarUrl = "https://avatars.githubusercontent.com/u/1"
    )

    private val testUser2 = UserInfo(
        id = 2,
        userName = "user2",
        avatarUrl = "https://avatars.githubusercontent.com/u/2"
    )

    private val testBookmarkedUsers = listOf(testUser1)

    @Before
    fun setup() {
        testDispatcher = StandardTestDispatcher()
        val dispatchers = AppDispatchers(
            io = testDispatcher,
            main = testDispatcher,
            default = testDispatcher
        )

        searchRepository = mockk()
        bookmarkRepository = mockk()
        componentContext = DefaultComponentContext(lifecycle = lifecycle)

        // Setup default repository behavior
        coEvery { bookmarkRepository.observeUserBookmarks() } returns flowOf(testBookmarkedUsers)
        coEvery { bookmarkRepository.addUserBookmark(any()) } returns Result.success(Unit)
        coEvery { bookmarkRepository.removeUserBookmark(any()) } returns Result.success(Unit)

        // Setup search repository with success scenario by default
        setupSearchRepository(Scenario.SUCCESS, dispatchers)

        val viewModelFactory: SearchViewModel.Factory = object : SearchViewModel.Factory {
            override fun create(query: String?): SearchViewModel {
                return SearchViewModel(query, searchRepository, bookmarkRepository, dispatchers)
            }
        }

        componentFactory = DefaultSearchComponentFactory(viewModelFactory)
    }

    private fun setupSearchRepository(scenario: Scenario, dispatchers: AppDispatchers) {
        every { searchRepository.searchUsers(any()) } answers {
            val query = firstArg<String>()
            createMockPagingFlow(query, scenario, dispatchers)
        }
    }

    private fun createMockPagingFlow(
        query: String,
        scenario: Scenario,
        dispatchers: AppDispatchers
    ): Flow<PagingData<UserInfo>> {
        return Pager(
            config = PagingConfig(
                pageSize = 30,
                enablePlaceholders = false,
                initialLoadSize = 30,
                prefetchDistance = 30
            ),
            pagingSourceFactory = {
                MockSearchPagingSource(
                    scenario = scenario,
                    networkDelay = 0, // No delay for tests
                    dispatchers = dispatchers
                )
            }
        ).flow.map { pagingData ->
            pagingData.map { it.asDomainModel() }
        }
    }

    @Test
    fun `component creation should initialize with empty query and users`() =
        runTest(testDispatcher) {
            // Given
            val userSelectedCallback = mockk<(UserInfo) -> Unit>(relaxed = true)

            // When
            val component = componentFactory.create(
                componentContext = componentContext,
                userSelected = userSelectedCallback
            )
            lifecycle.resume()
            advanceUntilIdle()

            // Then
            val query = component.query.first()
            assertEquals("", query)

            val users = component.users.asSnapshot()
            assertTrue(users.isEmpty())
        }

    @Test
    fun `search should update query and trigger user search`() = runTest(testDispatcher) {
        // Given
        val userSelectedCallback = mockk<(UserInfo) -> Unit>(relaxed = true)
        val searchQuery = "test"

        val component = componentFactory.create(
            componentContext = componentContext,
            userSelected = userSelectedCallback
        )
        lifecycle.resume()

        // When
        component.search(searchQuery)
        advanceUntilIdle()

        // Then
        val query = component.query.first()
        assertEquals(searchQuery, query)
        verify { searchRepository.searchUsers(searchQuery) }
    }

    @Test
    fun `search with blank query should clear users`() = runTest(testDispatcher) {
        // Given
        val userSelectedCallback = mockk<(UserInfo) -> Unit>(relaxed = true)
        val component = componentFactory.create(
            componentContext = componentContext,
            userSelected = userSelectedCallback
        )
        lifecycle.resume()

        // First search with valid query
        component.search("test")
        advanceUntilIdle()

        // When
        component.search("")
        advanceUntilIdle()

        // Then
        val query = component.query.first()
        assertEquals("", query)

        val users = component.users.asSnapshot()
        assertTrue(users.isEmpty())
    }

    @Test
    fun `search should return paginated users with bookmark status`() = runTest(testDispatcher) {
        // Given
        val userSelectedCallback = mockk<(UserInfo) -> Unit>(relaxed = true)
        val component = componentFactory.create(
            componentContext = componentContext,
            userSelected = userSelectedCallback
        )
        lifecycle.resume()

        // When
        component.search("test")
        advanceUntilIdle()

        // Then
        val users = component.users.asSnapshot {
            // Only request the initial items, don't trigger additional loading
            scrollTo(0)
        }
        assertTrue(users.isNotEmpty())
        // Note: asSnapshot() may load more than one page by default, so we check for at least 30
        assertTrue("Expected at least 30 users, got ${users.size}", users.size >= 30)

        // Check that bookmark status is properly set
        val userWithId1 = users.find { it.user.id == 1 }
        if (userWithId1 != null) {
            assertTrue(userWithId1.isBookmarked) // user1 is in testBookmarkedUsers
        }

        val userWithId2 = users.find { it.user.id == 2 }
        if (userWithId2 != null) {
            assertFalse(userWithId2.isBookmarked) // user2 is not bookmarked
        }
    }

    @Test
    fun `paging should load multiple pages successfully`() = runTest(testDispatcher) {
        // Given
        val userSelectedCallback = mockk<(UserInfo) -> Unit>(relaxed = true)
        val component = componentFactory.create(
            componentContext = componentContext,
            userSelected = userSelectedCallback
        )
        lifecycle.resume()

        // When
        component.search("test")
        advanceUntilIdle()

        // Then - Test loading multiple pages
        val users = component.users.asSnapshot {
            // This will trigger loading more pages
            scrollTo(50) // Scroll beyond first page
        }

        assertTrue(users.size >= 60) // Should have at least 2 pages (60 items)

        // Verify users are properly ordered
        assertEquals(1, users[0].user.id)
        assertEquals(30, users[29].user.id) // Last item of first page
        assertEquals(31, users[30].user.id) // First item of second page
    }

    @Test
    fun `paging should handle no data scenario`() = runTest(testDispatcher) {
        // Given
        setupSearchRepository(
            Scenario.NO_DATA,
            AppDispatchers(testDispatcher, testDispatcher, testDispatcher)
        )
        val userSelectedCallback = mockk<(UserInfo) -> Unit>(relaxed = true)
        val component = componentFactory.create(
            componentContext = componentContext,
            userSelected = userSelectedCallback
        )
        lifecycle.resume()

        // When
        component.search("test")
        advanceUntilIdle()

        // Then
        val users = component.users.asSnapshot()
        assertTrue(users.isEmpty())
    }

    @Test
    fun `onUserSelected should invoke callback`() = runTest(testDispatcher) {
        // Given
        val userSelectedCallback = mockk<(UserInfo) -> Unit>(relaxed = true)
        val component = componentFactory.create(
            componentContext = componentContext,
            userSelected = userSelectedCallback
        )
        lifecycle.resume()

        val userWithBookmark = UserWithBookmarkInfo(testUser1, false)

        // When
        component.onUserSelected(userWithBookmark)

        // Then
        verify { userSelectedCallback(testUser1) }
    }

    @Test
    fun `onBookmarkClick should add bookmark for non-bookmarked user`() = runTest(testDispatcher) {
        // Given
        val userSelectedCallback = mockk<(UserInfo) -> Unit>(relaxed = true)
        val component = componentFactory.create(
            componentContext = componentContext,
            userSelected = userSelectedCallback
        )
        lifecycle.resume()

        val userWithBookmark = UserWithBookmarkInfo(testUser2, false)

        // When
        component.onBookmarkClick(userWithBookmark)
        advanceUntilIdle()

        // Then
        coVerify { bookmarkRepository.addUserBookmark(testUser2) }
    }

    @Test
    fun `onBookmarkClick should remove bookmark for bookmarked user`() = runTest(testDispatcher) {
        // Given
        val userSelectedCallback = mockk<(UserInfo) -> Unit>(relaxed = true)
        val component = componentFactory.create(
            componentContext = componentContext,
            userSelected = userSelectedCallback
        )
        lifecycle.resume()

        val userWithBookmark = UserWithBookmarkInfo(testUser1, true)

        // When
        component.onBookmarkClick(userWithBookmark)
        advanceUntilIdle()

        // Then
        coVerify { bookmarkRepository.removeUserBookmark(testUser1) }
    }

    @Test
    fun `search should cancel previous search when new query is provided`() =
        runTest(testDispatcher) {
            // Given
            val userSelectedCallback = mockk<(UserInfo) -> Unit>(relaxed = true)
            val component = componentFactory.create(
                componentContext = componentContext,
                userSelected = userSelectedCallback
            )
            lifecycle.resume()

            // When
            component.search("first")
            component.search("second") // Should cancel first search
            advanceUntilIdle()

            // Then
            val query = component.query.first()
            assertEquals("second", query)
            verify { searchRepository.searchUsers("second") }
        }

    @Test
    fun `search with same query should not trigger new search`() = runTest(testDispatcher) {
        // Given
        val userSelectedCallback = mockk<(UserInfo) -> Unit>(relaxed = true)
        val component = componentFactory.create(
            componentContext = componentContext,
            userSelected = userSelectedCallback
        )
        lifecycle.resume()

        // When
        component.search("test")
        advanceUntilIdle()
        component.search("test") // Same query
        advanceUntilIdle()

        // Then - Should only call search once
        verify(exactly = 1) { searchRepository.searchUsers("test") }
    }

    @Test
    fun `bookmark status should update when bookmarks change`() = runTest(testDispatcher) {
        // Given
        val newBookmarkedUsers = listOf(testUser1, testUser2)
        coEvery { bookmarkRepository.observeUserBookmarks() } returns flowOf(
            testBookmarkedUsers, // Initial state
            newBookmarkedUsers   // Updated state
        )

        val userSelectedCallback = mockk<(UserInfo) -> Unit>(relaxed = true)
        val component = componentFactory.create(
            componentContext = componentContext,
            userSelected = userSelectedCallback
        )
        lifecycle.resume()

        // When
        component.search("test")
        advanceUntilIdle()

        // Then - Both users should now be bookmarked
        val users = component.users.asSnapshot()
        val userWithId1 = users.find { it.user.id == 1 }
        val userWithId2 = users.find { it.user.id == 2 }

        assertTrue(userWithId1?.isBookmarked ?: false)
        assertTrue(userWithId2?.isBookmarked ?: false)
    }

    @Test
    fun `factory should create different component instances`() = runTest(testDispatcher) {
        // Given
        val userSelectedCallback1 = mockk<(UserInfo) -> Unit>(relaxed = true)
        val userSelectedCallback2 = mockk<(UserInfo) -> Unit>(relaxed = true)

        val lifecycle2 = LifecycleRegistry()
        val componentContext2 = DefaultComponentContext(lifecycle = lifecycle2)

        // When
        val component1 = componentFactory.create(
            componentContext = componentContext,
            userSelected = userSelectedCallback1
        )

        val component2 = componentFactory.create(
            componentContext = componentContext2,
            userSelected = userSelectedCallback2
        )

        // Then
        assertTrue(component1 !== component2)
        assertTrue(component1.query !== component2.query)
        assertTrue(component1.users !== component2.users)
    }
}