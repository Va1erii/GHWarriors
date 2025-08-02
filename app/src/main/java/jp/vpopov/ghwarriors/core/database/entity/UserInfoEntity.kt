package jp.vpopov.ghwarriors.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import jp.vpopov.ghwarriors.core.domain.model.UserInfo

@Entity(tableName = "user_info_table")
data class UserInfoEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "user_name") val userName: String,
    @ColumnInfo(name = "avatar_name") val avatarUrl: String
)

fun UserInfoEntity.asDomainModel(): UserInfo {
    return UserInfo(
        id = id,
        userName = userName,
        avatarUrl = avatarUrl
    )
}

fun UserInfo.toEntity(): UserInfoEntity {
    return UserInfoEntity(
        id = id,
        userName = userName,
        avatarUrl = avatarUrl
    )
}