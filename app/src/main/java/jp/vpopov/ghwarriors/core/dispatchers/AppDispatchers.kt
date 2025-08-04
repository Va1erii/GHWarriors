package jp.vpopov.ghwarriors.core.dispatchers

import kotlinx.coroutines.CoroutineDispatcher

/**
 * Configuration class that holds coroutine dispatchers for different types of operations.
 * This centralizes dispatcher management and makes testing easier through dependency injection.
 */
data class AppDispatchers(
    /**
     * Dispatcher optimized for I/O operations like file access, database operations,
     * and disk-based caching.
     *
     * **Note**: When using Retrofit for network calls, avoid using this dispatcher
     * as OkHttp3 (which Retrofit uses internally) already handles I/O threading
     * with its own optimized executor service. Using Dispatchers.IO for Retrofit
     * calls adds unnecessary thread switching overhead.
     */
    val io: CoroutineDispatcher,

    /**
     * Dispatcher for UI operations and coroutines that need to run on the main thread.
     * Use this for updating UI components and handling user interactions.
     */
    val main: CoroutineDispatcher,

    /**
     * Dispatcher optimized for CPU-intensive operations like data processing,
     * calculations, and parsing. Uses a thread pool sized for CPU cores.
     */
    val default: CoroutineDispatcher
)