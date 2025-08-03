package jp.vpopov.ghwarriors

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import jp.vpopov.ghwarriors.core.logging.LoggingManager
import javax.inject.Inject

@HiltAndroidApp
class GHWarriorsApp : Application() {
    @Inject
    lateinit var loggingManager: LoggingManager

    override fun onCreate() {
        super.onCreate()
        loggingManager.setup()
    }
}