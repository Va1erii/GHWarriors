package jp.vpopov.ghwarriors.app

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.decompose.value.Value
import jakarta.inject.Inject
import jp.vpopov.ghwarriors.app.tabs.TabsComponent
import jp.vpopov.ghwarriors.core.deeplink.DeeplinkHandler
import jp.vpopov.ghwarriors.core.deeplink.DeeplinkValidator
import jp.vpopov.ghwarriors.feature.profile.presentation.component.ProfileComponent
import kotlinx.serialization.Serializable

interface RootComponent : DeeplinkHandler {
    val stack: Value<ChildStack<*, Child>>

    interface Factory {
        fun create(
            componentContext: ComponentContext,
            openExternalUrl: (String) -> Unit,
            deeplinkUrl: String? = null
        ): RootComponent
    }

    sealed class Child {
        class Tabs(val component: TabsComponent) : Child()
        class Profile(val component: ProfileComponent) : Child()
    }
}

class DefaultRootComponentFactory @Inject constructor(
    private val tabsComponentFactory: TabsComponent.Factory,
    private val profileComponentFactory: ProfileComponent.Factory
) : RootComponent.Factory {
    override fun create(
        componentContext: ComponentContext,
        openExternalUrl: (String) -> Unit,
        deeplinkUrl: String?,
    ): RootComponent {
        return DefaultRootComponent(
            componentContext = componentContext,
            deeplinkUrl = deeplinkUrl,
            openExternalUrl = openExternalUrl,
            tabsComponentFactory = tabsComponentFactory,
            profileComponentFactory = profileComponentFactory
        )
    }
}

class DefaultRootComponent(
    componentContext: ComponentContext,
    private val deeplinkUrl: String?,
    private val openExternalUrl: (String) -> Unit,
    private val tabsComponentFactory: TabsComponent.Factory,
    private val profileComponentFactory: ProfileComponent.Factory
) : RootComponent, ComponentContext by componentContext {
    private val navigation = StackNavigation<Config>()

    override val stack: Value<ChildStack<*, RootComponent.Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialStack = { getInitialConfig(deeplinkUrl) },
            handleBackButton = true,
            childFactory = ::createChild
        )

    private fun createChild(
        config: Config,
        componentContext: ComponentContext
    ): RootComponent.Child {
        return when (config) {
            Config.Tabs -> createTabsChild(componentContext)
            is Config.Profile -> createProfileChild(config, componentContext)
        }
    }

    private fun createTabsChild(
        componentContext: ComponentContext
    ): RootComponent.Child.Tabs {
        val component = tabsComponentFactory.create(
            componentContext = componentContext,
            onUserSelected = { user ->
                navigation.pushNew(Config.Profile(userId = user.id))
            }
        )
        return RootComponent.Child.Tabs(component)
    }

    private fun createProfileChild(
        config: Config.Profile,
        componentContext: ComponentContext
    ): RootComponent.Child.Profile {
        val component = profileComponentFactory.create(
            componentContext = componentContext,
            userId = config.userId,
            onRepositorySelected = { openExternalUrl(it.url) },
            onBackPressed = {
                navigation.pop()
            }
        )
        return RootComponent.Child.Profile(component)
    }

    private fun getInitialConfig(deeplinkUrl: String?): List<Config> {
        val profileConfig = deeplinkUrl?.let { createConfigFromDeeplink(it) }
        return listOfNotNull(Config.Tabs, profileConfig)
    }

    override fun handleDeeplink(deeplinkUrl: String) {
        val profileConfig = createConfigFromDeeplink(deeplinkUrl)
        if (profileConfig != null) {
            navigation.replaceAll(Config.Tabs, profileConfig)
        }
    }

    private fun createConfigFromDeeplink(deeplinkUrl: String): Config? {
        val isValid = DeeplinkValidator.isValidDeeplink(deeplinkUrl)
        return if (isValid) {
            val userId = deeplinkUrl.substringAfterLast("/").toIntOrNull()
            userId?.let { Config.Profile(it) }
        } else {
            null
        }
    }

    @Serializable
    sealed class Config {
        @Serializable
        data object Tabs : Config()

        @Serializable
        data class Profile(val userId: Int) : Config()
    }
}