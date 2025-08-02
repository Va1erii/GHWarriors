package jp.vpopov.ghwarriors.util

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource

@Immutable
sealed class ImageResource {
    @Immutable
    data class VectorResource(@DrawableRes val id: Int) : ImageResource()

    @Immutable
    data class VectorImage(val image: ImageVector) : ImageResource()
}

@Composable
fun ImageResource.imageVector(): ImageVector {
    return when (this) {
        is ImageResource.VectorImage -> image
        is ImageResource.VectorResource -> ImageVector.vectorResource(id)
    }
}