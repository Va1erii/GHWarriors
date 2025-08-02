package jp.vpopov.ghwarriors.app

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.children.ChildNavState.Status
import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.router.pages.Pages
import com.arkivanov.decompose.router.pages.PagesNavigation
import com.arkivanov.decompose.router.pages.childPages
import com.arkivanov.decompose.router.pages.select
import com.arkivanov.decompose.value.Value
import jakarta.inject.Inject
import jp.vpopov.ghwarriors.app.bookmark.BookmarkRootComponent
import jp.vpopov.ghwarriors.app.search.SearchRootComponent
import jp.vpopov.ghwarriors.app.search.SearchRootComponent.Deeplink
import jp.vpopov.ghwarriors.app.settings.SettingsRootComponent
import kotlinx.serialization.Serializable

interface RootComponent {
    val pages: Value<ChildPages<*, RootPageComponent>>

    fun selectPage(index: Int)

    interface Factory {
        fun create(
            componentContext: ComponentContext
        ): RootComponent
    }
}

class DefaultRootComponentFactory @Inject constructor(
    private val searchRootComponentFactory: SearchRootComponent.Factory,
    private val bookmarkRootComponentFactory: BookmarkRootComponent.Factory,
    private val settingsRootComponentFactory: SettingsRootComponent.Factory
) : RootComponent.Factory {
    override fun create(componentContext: ComponentContext): RootComponent {
        return DefaultRootComponent(
            componentContext,
            searchRootComponentFactory = searchRootComponentFactory,
            bookmarkRootComponentFactory = bookmarkRootComponentFactory,
            settingsRootComponentFactory = settingsRootComponentFactory
        )
    }
}

class DefaultRootComponent(
    componentContext: ComponentContext,
    private val searchRootComponentFactory: SearchRootComponent.Factory,
    private val bookmarkRootComponentFactory: BookmarkRootComponent.Factory,
    private val settingsRootComponentFactory: SettingsRootComponent.Factory
) : RootComponent, ComponentContext by componentContext {
    private val navigation = PagesNavigation<Config>()

    override val pages: Value<ChildPages<*, RootPageComponent>> =
        childPages(
            source = navigation,
            serializer = Config.serializer(),
            initialPages = {
                Pages(
                    items = listOf(Config.Search(), Config.Bookmarks, Config.Settings),
                    selectedIndex = 0,
                )
            },
            pageStatus = { index, pages ->
                when {
                    pages.selectedIndex == index -> Status.RESUMED
                    else -> Status.CREATED
                }
            }
        ) { config, ctx ->
            when (config) {
                is Config.Search -> createSearchComponent(config, ctx)
                Config.Bookmarks -> createBookmarkComponent(ctx)
                Config.Settings -> createSettingsComponent(ctx)
            }
        }

    private fun createSearchComponent(
        config: Config.Search,
        componentContext: ComponentContext
    ): SearchRootComponent {
        return searchRootComponentFactory.create(
            componentContext = componentContext,
            deeplink = config.userId?.let { Deeplink.Profile(it) }
        )
    }

    private fun createBookmarkComponent(
        componentContext: ComponentContext
    ): BookmarkRootComponent {
        return bookmarkRootComponentFactory.create(
            componentContext = componentContext
        )
    }

    private fun createSettingsComponent(
        componentContext: ComponentContext
    ): SettingsRootComponent {
        return settingsRootComponentFactory.create(componentContext)
    }


    override fun selectPage(index: Int) {
        navigation.select(index)
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