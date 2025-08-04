package jp.vpopov.ghwarriors.feature.profile.presentation.component

import androidx.paging.PagingData
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jp.vpopov.ghwarriors.core.data.user.UserRepository
import jp.vpopov.ghwarriors.core.data.userrepo.UserRepoInfoRepository
import jp.vpopov.ghwarriors.core.dispatchers.AppDispatchers
import jp.vpopov.ghwarriors.core.domain.model.UserProfileInfo
import jp.vpopov.ghwarriors.core.domain.model.UserRepoInfo
import jp.vpopov.ghwarriors.core.error.NetworkError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileComponentTest {

    private val lifecycle = LifecycleRegistry()
    private lateinit var componentFactory: ProfileComponent.Factory
    private lateinit var componentContext: ComponentContext
    private lateinit var userRepository: UserRepository
    private lateinit var userRepoInfoRepository: UserRepoInfoRepository
    private lateinit var testDispatcher: CoroutineDispatcher

    // Test data
    private val testUserId = 123
    private val testUserProfile = UserProfileInfo(
        userId = testUserId,
        name = "John Doe",
        userName = "johndoe",
        avatarUrl = "https://example.com/avatar.jpg",
        bio = "Software Developer",
        followersCount = 100,
        followingCount = 50,
        publicReposCount = 25
    )

    private val testRepo = UserRepoInfo(
        id = 1,
        name = "test-repo",
        fullName = "johndoe/test-repo",
        description = "A test repository",
        url = "https://github.com/johndoe/test-repo",
        language = "Kotlin",
        starsCount = 42,
        isFork = false
    )

    @Before
    fun setup() {
        testDispatcher = StandardTestDispatcher()
        val dispatchers = AppDispatchers(
            io = testDispatcher,
            main = testDispatcher,
            default = testDispatcher
        )

        userRepository = mockk()
        userRepoInfoRepository = mockk()
        componentContext = DefaultComponentContext(lifecycle = lifecycle)

        // Setup default repository behavior
        coEvery { userRepository.fetchProfile(testUserId) } returns Result.success(testUserProfile)
        every { userRepoInfoRepository.fetchRepositories(testUserId) } returns flowOf(
            PagingData.from(
                listOf(testRepo)
            )
        )
        every { userRepoInfoRepository.refreshRepositories() } returns Unit

        val viewModelFactory: ProfileViewModel.Factory = object : ProfileViewModel.Factory {
            override fun create(userId: Int): ProfileViewModel {
                return ProfileViewModel(userId, userRepository, userRepoInfoRepository, dispatchers)
            }
        }

        componentFactory = DefaultProfileComponentFactory(viewModelFactory)
    }

    @Test
    fun `component creation should initialize with loading state`() = runTest(testDispatcher) {
        // Given
        val onRepositorySelected = mockk<(UserRepoInfo) -> Unit>(relaxed = true)
        val onBackPressed = mockk<() -> Unit>(relaxed = true)

        // When
        val component = componentFactory.create(
            componentContext = componentContext,
            userId = testUserId,
            onRepositorySelected = onRepositorySelected,
            onBackPressed = onBackPressed
        )
        lifecycle.resume()

        // Then
        val initialState = component.model.first()
        assertEquals(ProfileState.Loading(testUserId), initialState)
    }

    @Test
    fun `model should transition to Success state when profile loads successfully`() =
        runTest(testDispatcher) {
            // Given
            val onRepositorySelected = mockk<(UserRepoInfo) -> Unit>(relaxed = true)
            val onBackPressed = mockk<() -> Unit>(relaxed = true)

            // When
            val component = componentFactory.create(
                componentContext = componentContext,
                userId = testUserId,
                onRepositorySelected = onRepositorySelected,
                onBackPressed = onBackPressed
            )
            lifecycle.resume()
            val stateDeferred = async {
                component.model.first { it is ProfileState.Success }
            }
            advanceUntilIdle()

            // Then
            val finalState = stateDeferred.await()
            assertTrue(finalState is ProfileState.Success)
            assertEquals(testUserId, finalState.userId)
            assertEquals(testUserProfile, (finalState as ProfileState.Success).userProfileInfo)
        }

    @Test
    fun `model should transition to Error state when profile loading fails`() =
        runTest(testDispatcher) {
            // Given
            val networkError = NetworkError.ServerError(500)
            coEvery { userRepository.fetchProfile(testUserId) } returns Result.failure(
                RuntimeException(
                    "Server error"
                )
            )

            val onRepositorySelected = mockk<(UserRepoInfo) -> Unit>(relaxed = true)
            val onBackPressed = mockk<() -> Unit>(relaxed = true)

            // When
            val component = componentFactory.create(
                componentContext = componentContext,
                userId = testUserId,
                onRepositorySelected = onRepositorySelected,
                onBackPressed = onBackPressed
            )
            lifecycle.resume()
            val stateDeferred = async {
                component.model.first { it is ProfileState.Error }
            }
            advanceUntilIdle()

            // Then
            val errorState = stateDeferred.await()
            assertTrue(errorState is ProfileState.Error)
            assertEquals(testUserId, errorState.userId)
        }

    @Test
    fun `onRepositorySelected should invoke callback`() = runTest(testDispatcher) {
        // Given
        val onRepositorySelected = mockk<(UserRepoInfo) -> Unit>(relaxed = true)
        val onBackPressed = mockk<() -> Unit>(relaxed = true)

        val component = componentFactory.create(
            componentContext = componentContext,
            userId = testUserId,
            onRepositorySelected = onRepositorySelected,
            onBackPressed = onBackPressed
        )
        lifecycle.resume()

        // When
        component.onRepositorySelected(testRepo)

        // Then
        verify { onRepositorySelected(testRepo) }
    }

    @Test
    fun `onBackButtonPressed should invoke callback`() = runTest(testDispatcher) {
        // Given
        val onRepositorySelected = mockk<(UserRepoInfo) -> Unit>(relaxed = true)
        val onBackPressed = mockk<() -> Unit>(relaxed = true)

        val component = componentFactory.create(
            componentContext = componentContext,
            userId = testUserId,
            onRepositorySelected = onRepositorySelected,
            onBackPressed = onBackPressed
        )
        lifecycle.resume()

        // When
        component.onBackButtonPressed()

        // Then
        verify { onBackPressed() }
    }

    @Test
    fun `onRefreshRepositories should refresh repositories only`() = runTest(testDispatcher) {
        // Given
        val onRepositorySelected = mockk<(UserRepoInfo) -> Unit>(relaxed = true)
        val onBackPressed = mockk<() -> Unit>(relaxed = true)

        val component = componentFactory.create(
            componentContext = componentContext,
            userId = testUserId,
            onRepositorySelected = onRepositorySelected,
            onBackPressed = onBackPressed
        )
        lifecycle.resume()

        // When
        component.onRefreshRepositories()

        // Then
        verify(atLeast = 1) { userRepoInfoRepository.refreshRepositories() }
    }

    @Test
    fun `component should handle error recovery on refresh`() = runTest(testDispatcher) {
        // Given
        coEvery { userRepository.fetchProfile(testUserId) } returnsMany listOf(
            Result.failure(RuntimeException("Initial error")),
            Result.success(testUserProfile)
        )

        val onRepositorySelected = mockk<(UserRepoInfo) -> Unit>(relaxed = true)
        val onBackPressed = mockk<() -> Unit>(relaxed = true)

        val component = componentFactory.create(
            componentContext = componentContext,
            userId = testUserId,
            onRepositorySelected = onRepositorySelected,
            onBackPressed = onBackPressed
        )
        lifecycle.resume()

        // Wait for initial Error state
        val errorState = component.model.first { it is ProfileState.Error }
        assertTrue(errorState is ProfileState.Error)

        // When
        component.onRefresh()

        // Then - Should recover to Success state
        val successState = component.model.first { it is ProfileState.Success }
        assertEquals(testUserProfile, (successState as ProfileState.Success).userProfileInfo)
    }
}