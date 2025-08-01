package jp.vpopov.ghwarriors.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import jp.vpopov.ghwarriors.core.database.entity.UserInfoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserInfoDao {
    @Query("SELECT * FROM user_info")
    fun observeAll(): Flow<List<UserInfoEntity>>

    @Query("SELECT * FROM user_info WHERE id = :id")
    suspend fun getById(id: Int): UserInfoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg users: UserInfoEntity)

    @Delete
    suspend fun delete(user: UserInfoEntity)
}