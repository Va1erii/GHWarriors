package jp.vpopov.ghwarriors

import android.app.Application
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.util.DebugLogger
import dagger.hilt.android.HiltAndroidApp
import jp.vpopov.ghwarriors.core.logging.LoggingManager
import javax.inject.Inject

@HiltAndroidApp
class GHWarriorsApp : Application() {
    @Inject
    lateinit var loggingManager: LoggingManager

    override fun onCreate() {
        super.onCreate()
        val imageLoader = ImageLoader.Builder(this)
            .logger(DebugLogger())
            .build()
        SingletonImageLoader.setSafe { imageLoader }
        loggingManager.setup()
    }
}