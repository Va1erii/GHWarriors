package jp.vpopov.ghwarriors.core.extension

import kotlin.coroutines.cancellation.CancellationException

/**
 * Extension function for Result that ensures CancellationException is properly re-thrown.
 *
 * This is crucial when using runCatching in suspend functions, as runCatching consumes
 * ALL exceptions including CancellationException. In coroutines, CancellationException
 * should never be caught and must be re-thrown to maintain proper cancellation behavior.
 *
 * **Important**: Always use this extension when wrapping suspend functions with runCatching
 * to ensure coroutine cancellation works correctly.
 *
 * @return The same Result, but with CancellationException re-thrown if present
 * @throws CancellationException if the Result contains a CancellationException
 */
fun <T> Result<T>.throwOnCancellation(): Result<T> {
    return onFailure { throwable ->
        if (throwable is CancellationException) {
            throw throwable
        }
    }
}