package jp.vpopov.ghwarriors.feature.bookmark.presentation.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import jp.vpopov.ghwarriors.core.data.bookmark.BookmarkRepository
import jp.vpopov.ghwarriors.core.dispatchers.AppDispatchers
import jp.vpopov.ghwarriors.core.domain.model.UserInfo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class BookmarkComponentTest {
    private val lifecycle = LifecycleRegistry()
    private lateinit var componentFactory: BookmarkComponent.Factory
    private lateinit var componentContext: ComponentContext
    private lateinit var bookmarkRepository: BookmarkRepository

    // Test data
    private val testUser1 = UserInfo(
        id = 1,
        userName = "testuser1",
        avatarUrl = "https://example.com/avatar1.jpg"
    )

    private val testUser2 = UserInfo(
        id = 2,
        userName = "testuser2",
        avatarUrl = "https://example.com/avatar2.jpg"
    )

    private val testBookmarks = listOf(testUser1, testUser2)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        val testDispatcher = UnconfinedTestDispatcher()
        val dispatchers = AppDispatchers(
            io = testDispatcher,
            main = testDispatcher,
            default = testDispatcher
        )
        bookmarkRepository = mockk()
        componentContext = DefaultComponentContext(lifecycle = lifecycle)

        // Setup default repository behavior
        coEvery { bookmarkRepository.observeUserBookmarks() } returns flowOf(testBookmarks)
        coEvery { bookmarkRepository.removeUserBookmark(any()) } returns Result.success(Unit)

        val viewModelFactory: BookmarkViewModel.Factory = object : BookmarkViewModel.Factory {
            override fun create(): BookmarkViewModel {
                return BookmarkViewModel(bookmarkRepository, dispatchers)
            }
        }
        componentFactory = DefaultBookmarkComponentFactory(
            viewModelFactory = viewModelFactory
        )
    }

    @Test
    fun `bookmarks flow should emit repository data`() = runTest {
        // Given
        val userSelectedCallback = mockk<(UserInfo) -> Unit>(relaxed = true)
        val component = componentFactory.create(
            componentContext = componentContext,
            userSelected = userSelectedCallback
        )
        lifecycle.resume()

        // When & Then
        val bookmarks = component.bookmarks.first()
        assertEquals(testBookmarks, bookmarks)
        assertEquals(2, bookmarks.size)
        assertEquals(testUser1, bookmarks[0])
        assertEquals(testUser2, bookmarks[1])
    }

    @Test
    fun `onUserSelected should invoke callback`() = runTest {
        // Given
        val userSelectedCallback = mockk<(UserInfo) -> Unit>(relaxed = true)
        val component = componentFactory.create(
            componentContext = componentContext,
            userSelected = userSelectedCallback
        )
        lifecycle.resume()

        // When
        component.onUserSelected(testUser1)

        // Then
        verify { userSelectedCallback(testUser1) }
    }


    @Test
    fun `onRemoveBookmark should call repository removeUserBookmark`() = runTest {
        // Given
        val userSelectedCallback = mockk<(UserInfo) -> Unit>(relaxed = true)
        val component = componentFactory.create(
            componentContext = componentContext,
            userSelected = userSelectedCallback
        )
        lifecycle.resume()

        // When
        component.onRemoveBookmark(testUser1)

        // Then
        coVerify { bookmarkRepository.removeUserBookmark(testUser1) }
    }

    @Test
    fun `onRemoveBookmark should handle repository failure gracefully`() = runTest {
        // Given
        val exception = RuntimeException("Remove bookmark failed")
        coEvery { bookmarkRepository.removeUserBookmark(testUser1) } returns Result.failure(
            exception
        )

        val userSelectedCallback = mockk<(UserInfo) -> Unit>(relaxed = true)
        val component = componentFactory.create(
            componentContext = componentContext,
            userSelected = userSelectedCallback
        )
        lifecycle.resume()

        // When
        component.onRemoveBookmark(testUser1)

        // Then
        coVerify { bookmarkRepository.removeUserBookmark(testUser1) }
    }

    @Test
    fun `multiple user selections should work correctly`() = runTest {
        // Given
        val userSelectedCallback = mockk<(UserInfo) -> Unit>(relaxed = true)
        val component = componentFactory.create(
            componentContext = componentContext,
            userSelected = userSelectedCallback
        )
        lifecycle.resume()

        // When
        component.onUserSelected(testUser1)
        component.onUserSelected(testUser2)

        // Then
        verify { userSelectedCallback(testUser1) }
        verify { userSelectedCallback(testUser2) }
    }

    @Test
    fun `component should handle repository exceptions in bookmarks flow`() = runTest {
        // Given
        val exception = RuntimeException("Repository error")
        clearMocks(bookmarkRepository)
        coEvery { bookmarkRepository.observeUserBookmarks() } answers {
            flow { emit(throw exception) }
        }

        val userSelectedCallback = mockk<(UserInfo) -> Unit>(relaxed = true)

        // When
        val component = componentFactory.create(
            componentContext = componentContext,
            userSelected = userSelectedCallback
        )
        lifecycle.resume()

        // Then - Component should handle the exception gracefully
        // The catch block in BookmarkViewModel should emit empty list
        val bookmarks = component.bookmarks.first()
        assertEquals(0, bookmarks.size)
    }
}