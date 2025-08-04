package jp.vpopov.ghwarriors.core.logging

import kotlin.reflect.KClass

/**
 * Interface for managing logging system initialization and logger creation.
 * Implementations handle the setup of logging infrastructure and provide tagged loggers.
 */
interface LoggingManager {
    /**
     * Initializes the logging system.
     * This should be called once during application startup.
     */
    fun setup()

    /**
     * Creates a logger with the specified tag.
     *
     * @param tag The tag to associate with log messages from this logger
     * @return A Logger instance configured with the specified tag
     */
    fun withTag(tag: String): Logger
}

/**
 * Creates a logger with a tag derived from the class name.
 * This is a convenience extension that uses the simple class name as the tag.
 *
 * @param clazz The class to use for generating the tag
 * @return A Logger instance configured with the class name as tag
 */
fun LoggingManager.withTag(clazz: KClass<*>): Logger = withTag(clazz.java.simpleName)