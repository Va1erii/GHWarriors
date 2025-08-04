package jp.vpopov.ghwarriors.core.logging

/**
 * A no-operation implementation of LoggingManager that discards all log messages.
 * This is useful for production builds where logging should be disabled for performance
 * or when no logging implementation is configured.
 */
object NoOpLoggingManager : LoggingManager {
    override fun setup() {
        // No-op implementation - nothing to set up
    }

    override fun withTag(tag: String): Logger {
        return NoOpLogger
    }
}

/**
 * A no-operation Logger implementation that discards all log messages.
 * All logging operations are no-ops, providing zero overhead when logging is disabled.
 */
private object NoOpLogger : Logger {
    override fun log(priority: Int, throwable: Throwable?, message: () -> String) {
        // No-op implementation - message lambda is never called, avoiding string creation
    }
}