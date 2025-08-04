package jp.vpopov.ghwarriors.core.logging

/**
 * Global logging facade that delegates to a configurable LoggingManager implementation.
 * This allows for easy switching between different logging implementations (e.g., Timber, console, no-op).
 *
 * By default, uses NoOpLoggingManager which discards all log messages.
 * The delegate should be set during application initialization.
 */
object Logging : LoggingManager {
    /**
     * The actual LoggingManager implementation that handles logging operations.
     * Defaults to NoOpLoggingManager which discards all logs.
     */
    var delegate: LoggingManager = NoOpLoggingManager

    override fun setup() {
        delegate.setup()
    }

    override fun withTag(tag: String): Logger {
        return delegate.withTag(tag)
    }
}