package jp.vpopov.ghwarriors.util

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource

/**
 * Sealed class representing different types of painter resources in Compose.
 * This abstraction allows for unified handling of drawable resources and vector images.
 */
@Immutable
sealed class PainterResource {
    /**
     * Represents a drawable resource from the app's resources.
     *
     * @param id The drawable resource ID
     */
    @Immutable
    data class DrawableResource(@DrawableRes val id: Int) : PainterResource()

    /**
     * Represents a vector image that can be painted in Compose.
     *
     * @param image The ImageVector to be painted
     */
    @Immutable
    data class VectorImage(val image: ImageVector) : PainterResource()
}

/**
 * Extension function that converts a PainterResource to a Compose Painter.
 *
 * This function handles the conversion of different resource types:
 * - DrawableResource: Uses painterResource() to load from app resources
 * - VectorImage: Uses rememberVectorPainter() to create a painter from ImageVector
 *
 * @return A Painter that can be used in Compose UI components
 */
@Stable
@Composable
fun PainterResource.painter(): Painter {
    return when (this) {
        is PainterResource.VectorImage -> rememberVectorPainter(image)
        is PainterResource.DrawableResource -> painterResource(id)
    }
}