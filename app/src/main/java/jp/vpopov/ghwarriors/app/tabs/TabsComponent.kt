package jp.vpopov.ghwarriors.app.tabs

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import jakarta.inject.Inject
import jp.vpopov.ghwarriors.app.settings.SettingsRootComponent
import jp.vpopov.ghwarriors.core.domain.model.UserInfo
import jp.vpopov.ghwarriors.feature.bookmark.presentation.component.BookmarkComponent
import jp.vpopov.ghwarriors.feature.search.presentation.component.SearchComponent
import kotlinx.serialization.Serializable

interface TabsComponent {
    val stack: Value<ChildStack<*, Child>>
    fun selectTab(tab: Tab)

    interface Factory {
        fun create(
            componentContext: ComponentContext,
            onUserSelected: (UserInfo) -> Unit
        ): TabsComponent
    }

    sealed class Child {
        abstract val tab: Tab

        class Search(val component: SearchComponent) : Child() {
            override val tab: Tab = Tab.Search
        }

        class Bookmark(val component: BookmarkComponent) : Child() {
            override val tab: Tab = Tab.Bookmarks
        }

        class Settings(val component: SettingsRootComponent) : Child() {
            override val tab: Tab = Tab.Settings
        }
    }

    enum class Tab(val index: Int) {
        Search(0),
        Bookmarks(1),
        Settings(2)
    }
}

class DefaultTabsComponentFactory @Inject constructor(
    private val searchComponentFactory: SearchComponent.Factory,
    private val bookmarkComponentFactory: BookmarkComponent.Factory,
    private val settingsRootComponentFactory: SettingsRootComponent.Factory
) : TabsComponent.Factory {
    override fun create(
        componentContext: ComponentContext,
        onUserSelected: (UserInfo) -> Unit
    ): TabsComponent {
        return DefaultTabsComponent(
            componentContext = componentContext,
            onUserSelected = onUserSelected,
            searchComponentFactory = searchComponentFactory,
            bookmarkComponentFactory = bookmarkComponentFactory,
            settingsRootComponentFactory = settingsRootComponentFactory
        )
    }
}

class DefaultTabsComponent(
    componentContext: ComponentContext,
    private val onUserSelected: (UserInfo) -> Unit,
    private val searchComponentFactory: SearchComponent.Factory,
    private val bookmarkComponentFactory: BookmarkComponent.Factory,
    private val settingsRootComponentFactory: SettingsRootComponent.Factory
) : TabsComponent, ComponentContext by componentContext {
    private val navigation = StackNavigation<Config>()

    override val stack: Value<ChildStack<*, TabsComponent.Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialStack = { listOf(Config.Search) },
            handleBackButton = false,
            childFactory = ::createChild
        )

    private fun createChild(
        config: Config,
        componentContext: ComponentContext
    ): TabsComponent.Child {
        return when (config) {
            Config.Search -> createSearchChild(componentContext)
            Config.Bookmarks -> createBookmarkChild(componentContext)
            Config.Settings -> createSettingsChild(componentContext)
        }
    }

    private fun createSearchChild(
        componentContext: ComponentContext
    ): TabsComponent.Child.Search {
        val component = searchComponentFactory.create(
            componentContext = componentContext,
            userSelected = onUserSelected
        )
        return TabsComponent.Child.Search(component)
    }

    private fun createBookmarkChild(
        componentContext: ComponentContext
    ): TabsComponent.Child.Bookmark {
        val component = bookmarkComponentFactory.create(
            componentContext = componentContext,
            userSelected = onUserSelected
        )
        return TabsComponent.Child.Bookmark(component)
    }

    private fun createSettingsChild(
        componentContext: ComponentContext
    ): TabsComponent.Child.Settings {
        val component = settingsRootComponentFactory.create(componentContext)
        return TabsComponent.Child.Settings(component)
    }

    override fun selectTab(tab: TabsComponent.Tab) {
        val config = when (tab) {
            TabsComponent.Tab.Search -> Config.Search
            TabsComponent.Tab.Bookmarks -> Config.Bookmarks
            TabsComponent.Tab.Settings -> Config.Settings
        }
        navigation.bringToFront(config)
    }

    @Serializable
    sealed class Config {
        @Serializable
        data object Search : Config()

        @Serializable
        data object Bookmarks : Config()

        @Serializable
        data object Settings : Config()
    }
}