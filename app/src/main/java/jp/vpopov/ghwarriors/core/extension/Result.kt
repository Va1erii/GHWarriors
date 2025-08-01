package jp.vpopov.ghwarriors.core.extension

import kotlin.coroutines.cancellation.CancellationException

fun <T> Result<T>.throwCancellation(): Result<T> {
    return onFailure { throwable ->
        if (throwable is CancellationException) {
            throw throwable
        }
    }
}