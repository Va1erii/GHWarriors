package jp.vpopov.ghwarriors.app

import com.arkivanov.decompose.Child
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import jakarta.inject.Inject
import jp.vpopov.ghwarriors.app.bookmark.BookmarkRootComponent
import jp.vpopov.ghwarriors.app.search.SearchRootComponent
import jp.vpopov.ghwarriors.app.settings.SettingsRootComponent
import kotlinx.serialization.Serializable

interface RootComponent {
    val stack: Value<ChildStack<*, Child>>

    fun selectTab(tab: Tab)

    interface Factory {
        fun create(
            componentContext: ComponentContext,
            deeplinkUrl: String? = null
        ): RootComponent
    }

    sealed class Child {
        abstract val tab: Tab
        abstract val component: RootPageComponent

        class Search(override val component: SearchRootComponent) : Child() {
            override val tab: Tab = Tab.Search
        }

        class Bookmark(override val component: BookmarkRootComponent) : Child() {
            override val tab: Tab = Tab.Bookmarks
        }

        class Settings(override val component: SettingsRootComponent) : Child() {
            override val tab: Tab = Tab.Settings
        }
    }

    enum class Tab(val index: Int) {
        Search(0),
        Bookmarks(1),
        Settings(2)
    }
}

class DefaultRootComponentFactory @Inject constructor(
    private val searchRootComponentFactory: SearchRootComponent.Factory,
    private val bookmarkRootComponentFactory: BookmarkRootComponent.Factory,
    private val settingsRootComponentFactory: SettingsRootComponent.Factory
) : RootComponent.Factory {
    override fun create(
        componentContext: ComponentContext,
        deeplinkUrl: String?
    ): RootComponent {
        return DefaultRootComponent(
            componentContext = componentContext,
            deeplinkUrl = deeplinkUrl,
            searchRootComponentFactory = searchRootComponentFactory,
            bookmarkRootComponentFactory = bookmarkRootComponentFactory,
            settingsRootComponentFactory = settingsRootComponentFactory
        )
    }
}

class DefaultRootComponent(
    componentContext: ComponentContext,
    private val deeplinkUrl: String?,
    private val searchRootComponentFactory: SearchRootComponent.Factory,
    private val bookmarkRootComponentFactory: BookmarkRootComponent.Factory,
    private val settingsRootComponentFactory: SettingsRootComponent.Factory
) : RootComponent, ComponentContext by componentContext {
    private val navigation = StackNavigation<Config>()

    override val stack: Value<ChildStack<*, RootComponent.Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialStack = { listOf(getInitialConfig(deeplinkUrl)) },
            handleBackButton = false,
            childFactory = ::createChild
        )

    private fun createChild(
        config: Config,
        componentContext: ComponentContext
    ): RootComponent.Child {
        return when (config) {
            is Config.Search -> createSearchChild(config, componentContext)
            Config.Bookmarks -> createBookmarkChild(componentContext)
            Config.Settings -> createSettingsChild(componentContext)
        }
    }

    private fun createSearchChild(
        config: Config.Search,
        componentContext: ComponentContext
    ): RootComponent.Child.Search {
        val component = searchRootComponentFactory.create(
            componentContext = componentContext,
            userId = config.userId
        )
        return RootComponent.Child.Search(component)
    }

    private fun createBookmarkChild(
        componentContext: ComponentContext
    ): RootComponent.Child.Bookmark {
        val component = bookmarkRootComponentFactory.create(componentContext)
        return RootComponent.Child.Bookmark(component)
    }

    private fun createSettingsChild(
        componentContext: ComponentContext
    ): RootComponent.Child.Settings {
        val component = settingsRootComponentFactory.create(componentContext)
        return RootComponent.Child.Settings(component)
    }

    private fun getInitialConfig(deeplinkUrl: String?): Config {
        return Config.Search()
    }

    override fun selectTab(tab: RootComponent.Tab) {
        val config = when (tab) {
            RootComponent.Tab.Search -> Config.Search()
            RootComponent.Tab.Bookmarks -> Config.Bookmarks
            RootComponent.Tab.Settings -> Config.Settings
        }
        navigation.bringToFront(config)
    }

    @Serializable
    sealed class Config {
        @Serializable
        data class Search(val userId: Int? = null) : Config()

        @Serializable
        data object Bookmarks : Config()

        @Serializable
        data object Settings : Config()
    }
}