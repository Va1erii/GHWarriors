package jp.vpopov.ghwarriors.core.deeplink

/**
 * Interface for handling deeplink navigation within the application.
 * Implementations should parse the deeplink URL and navigate to the appropriate screen.
 */
interface DeeplinkHandler {
    /**
     * Processes a deeplink URL and navigates to the corresponding screen.
     *
     * @param deeplinkUrl The deeplink URL to handle (e.g., "ghwarriors://profile/123")
     */
    fun handleDeeplink(deeplinkUrl: String)
}

/**
 * Utility object for validating deeplink URLs against supported patterns.
 * This ensures only valid deeplinks are processed by the application.
 */
object DeeplinkValidator {
    /**
     * List of regex patterns that define valid deeplink formats.
     * Currently supports:
     * - HTTPS web links: https://ghwarriors.app/profile/{userId}
     * - Custom scheme links: ghwarriors://profile/{userId}
     */
    private val SUPPORTED_PATTERNS = listOf(
        "https://ghwarriors.app/profile/\\d+",
        "ghwarriors://profile/\\d+",
    )

    /**
     * Validates whether a given URL matches any of the supported deeplink patterns.
     *
     * @param url The URL to validate
     * @return true if the URL matches a supported deeplink pattern, false otherwise
     */
    fun isValidDeeplink(url: String): Boolean {
        return SUPPORTED_PATTERNS.any { pattern ->
            url.matches(Regex(pattern))
        }
    }
}