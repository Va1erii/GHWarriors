package jp.vpopov.ghwarriors.core.data.bookmark

import jp.vpopov.ghwarriors.core.database.dao.UserInfoDao
import jp.vpopov.ghwarriors.core.database.entity.asDomainModel
import jp.vpopov.ghwarriors.core.database.entity.toEntity
import jp.vpopov.ghwarriors.core.domain.model.UserInfo
import jp.vpopov.ghwarriors.core.extension.throwCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface BookmarkRepository {
    suspend fun getBookmarks(): Flow<List<UserInfo>>
    suspend fun addBookmark(userInfo: UserInfo): Result<Unit>
    suspend fun removeBookmark(userInfo: UserInfo): Result<Unit>
}

class BookmarkRepositoryImpl @Inject constructor(
    private val userInfoDao: UserInfoDao
) : BookmarkRepository {

    override suspend fun getBookmarks(): Flow<List<UserInfo>> {
        return userInfoDao.observeAll()
            .map { data -> data.map { it.asDomainModel() } }
    }

    override suspend fun addBookmark(userInfo: UserInfo): Result<Unit> {
        return runCatching { userInfoDao.insertAll(userInfo.toEntity()) }
            .throwCancellation()
    }

    override suspend fun removeBookmark(userInfo: UserInfo): Result<Unit> {
        return runCatching { userInfoDao.delete(userInfo.toEntity()) }
            .throwCancellation()
    }
}