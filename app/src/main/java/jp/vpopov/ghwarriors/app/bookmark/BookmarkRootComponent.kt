package jp.vpopov.ghwarriors.app.bookmark

import com.arkivanov.decompose.ComponentContext
import jakarta.inject.Inject

interface BookmarkRootComponent {
    interface Factory {
        fun create(
            componentContext: ComponentContext,
        ): BookmarkRootComponent
    }
}

class DefaultBookmarkRootComponentFactory @Inject constructor() : BookmarkRootComponent.Factory {
    override fun create(
        componentContext: ComponentContext
    ): BookmarkRootComponent {
        return DefaultBookmarkRootComponent(componentContext)
    }

}

class DefaultBookmarkRootComponent(
    componentContext: ComponentContext
) : BookmarkRootComponent, ComponentContext by componentContext {
}