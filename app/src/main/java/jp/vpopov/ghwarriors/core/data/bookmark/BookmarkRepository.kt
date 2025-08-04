package jp.vpopov.ghwarriors.core.data.bookmark

import jp.vpopov.ghwarriors.core.database.dao.UserInfoDao
import jp.vpopov.ghwarriors.core.database.entity.asDomainModel
import jp.vpopov.ghwarriors.core.database.entity.toEntity
import jp.vpopov.ghwarriors.core.dispatchers.AppDispatchers
import jp.vpopov.ghwarriors.core.domain.model.UserInfo
import jp.vpopov.ghwarriors.core.extension.throwOnCancellation
import jp.vpopov.ghwarriors.core.logging.Logging
import jp.vpopov.ghwarriors.core.logging.d
import jp.vpopov.ghwarriors.core.logging.withTagLazy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface BookmarkRepository {
    fun observeUserBookmarks(): Flow<List<UserInfo>>
    suspend fun addUserBookmark(userInfo: UserInfo): Result<Unit>
    suspend fun removeUserBookmark(userInfo: UserInfo): Result<Unit>
}

class BookmarkRepositoryImpl @Inject constructor(
    private val userInfoDao: UserInfoDao,
    private val dispatchers: AppDispatchers,
) : BookmarkRepository {
    private val logger by Logging.withTagLazy(BookmarkRepository::class)

    override fun observeUserBookmarks(): Flow<List<UserInfo>> {
        return userInfoDao.observeAll()
            .map { data -> data.map { it.asDomainModel() } }
            .flowOn(dispatchers.io)
    }

    override suspend fun addUserBookmark(userInfo: UserInfo): Result<Unit> {
        logger.d { "Add user bookmark, userInfo=($userInfo)" }
        return withContext(dispatchers.io) {
            runCatching { userInfoDao.insertAll(userInfo.toEntity()) }
                .throwOnCancellation()
        }
    }

    override suspend fun removeUserBookmark(userInfo: UserInfo): Result<Unit> {
        logger.d { "Remove user bookmark, userInfo=($userInfo)" }
        return withContext(dispatchers.io) {
            runCatching { userInfoDao.delete(userInfo.toEntity()) }
                .throwOnCancellation()
        }
    }
}