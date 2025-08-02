package jp.vpopov.ghwarriors.core.deeplink

interface DeeplinkHandler {
    fun handleDeeplink(deeplinkUrl: String)
}

object DeeplinkValidator {
    private val SUPPORTED_PATTERNS = listOf(
        "https://ghwarriors.app/profile/\\d+",
        "ghwarriors://profile/\\d+",
    )

    fun isValidDeeplink(url: String): Boolean {
        return SUPPORTED_PATTERNS.any { pattern ->
            url.matches(Regex(pattern))
        }
    }
}