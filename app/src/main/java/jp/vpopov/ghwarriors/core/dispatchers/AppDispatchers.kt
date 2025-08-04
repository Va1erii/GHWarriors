package jp.vpopov.ghwarriors.core.dispatchers

import kotlinx.coroutines.CoroutineDispatcher

data class AppDispatchers(
    val io: CoroutineDispatcher,
    val main: CoroutineDispatcher,
    val databaseIO: CoroutineDispatcher
)