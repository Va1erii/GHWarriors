package jp.vpopov.ghwarriors.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import jp.vpopov.ghwarriors.core.database.dao.UserInfoDao
import jp.vpopov.ghwarriors.core.database.entity.UserInfoEntity

@Database(
    entities = [UserInfoEntity::class],
    version = 1,
)
abstract class GHWDatabase : RoomDatabase() {
    abstract fun userInfoDao(): UserInfoDao
}