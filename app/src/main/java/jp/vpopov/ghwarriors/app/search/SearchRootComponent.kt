package jp.vpopov.ghwarriors.app.search

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.subscribe
import jp.vpopov.ghwarriors.app.RootPageComponent
import jp.vpopov.ghwarriors.feature.profile.presentation.component.ProfileComponent
import jp.vpopov.ghwarriors.feature.search.presentation.component.SearchComponent
import kotlinx.serialization.Serializable
import javax.inject.Inject

interface SearchRootComponent : RootPageComponent {
    val childStack: Value<ChildStack<*, Child>>

    sealed class Child {
        class Search(val component: SearchComponent) : Child()
        class Profile(val component: ProfileComponent) : Child()
    }

    interface Factory {
        fun create(
            componentContext: ComponentContext,
            userId: Int? = null,
        ): SearchRootComponent
    }
}

class DefaultSearchRootComponentFactory @Inject constructor(
    private val searchComponentFactory: SearchComponent.Factory,
    private val profileComponentFactory: ProfileComponent.Factory
) : SearchRootComponent.Factory {
    override fun create(
        componentContext: ComponentContext,
        userId: Int?
    ): SearchRootComponent = DefaultSearchRootComponent(
        componentContext = componentContext,
        userId = userId,
        searchComponentFactory = searchComponentFactory,
        profileComponentFactory = profileComponentFactory
    )
}

class DefaultSearchRootComponent(
    componentContext: ComponentContext,
    private val userId: Int?,
    private val searchComponentFactory: SearchComponent.Factory,
    private val profileComponentFactory: ProfileComponent.Factory
) : SearchRootComponent, ComponentContext by componentContext {
    private val navigation = StackNavigation<Config>()
    private val _showNavBar = MutableValue(true)
    override val childStack: Value<ChildStack<*, SearchRootComponent.Child>> = childStack(
        source = navigation,
        serializer = Config.serializer(),
        initialStack = {
            if (userId != null) {
                listOf(Config.Search, Config.Profile(userId = userId))
            } else {
                listOf(Config.Search)
            }
        },
        handleBackButton = true,
        childFactory = ::createChild,
    )
    override val showNavBar: Value<Boolean> = _showNavBar

    init {
        childStack.subscribe(lifecycle) {
            val showNavBar = when (it.active.configuration) {
                is Config.Search -> true
                else -> false
            }
            _showNavBar.value = showNavBar
        }
    }

    private fun createChild(
        config: Config,
        componentContext: ComponentContext
    ): SearchRootComponent.Child {
        return when (config) {
            is Config.Profile -> {
                val component = profileComponentFactory.create(
                    componentContext = componentContext,
                    userId = config.userId,
                    onRepositorySelected = {},
                    onBackPressed = {
                        navigation.pop()
                    }
                )
                SearchRootComponent.Child.Profile(component)
            }

            is Config.Search -> {
                val component = searchComponentFactory.create(
                    componentContext = componentContext,
                    userSelected = {
                        navigation.pushNew(Config.Profile(userId = it.id))
                    },
                )
                SearchRootComponent.Child.Search(component)
            }
        }
    }


    @Serializable
    sealed class Config {
        @Serializable
        data object Search : Config()

        @Serializable
        data class Profile(val userId: Int) : Config()
    }
}