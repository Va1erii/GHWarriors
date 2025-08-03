package jp.vpopov.ghwarriors.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat
import coil3.DrawableImage
import coil3.Image

@Immutable
sealed class PainterResource {
    @Immutable
    data class DrawableResource(@DrawableRes val id: Int) : PainterResource()

    @Immutable
    data class VectorImage(val image: ImageVector) : PainterResource()
}

@Stable
@Composable
fun PainterResource.painter(): Painter {
    return when (this) {
        is PainterResource.VectorImage -> rememberVectorPainter(image)
        is PainterResource.DrawableResource -> painterResource(id)
    }
}