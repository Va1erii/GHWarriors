package jp.vpopov.ghwarriors.core.data.repository

import jp.vpopov.ghwarriors.core.data.repository.dto.asDomainModel
import jp.vpopov.ghwarriors.core.domain.model.UserRepositoryInfo
import jp.vpopov.ghwarriors.core.extension.throwCancellation
import javax.inject.Inject

interface UserRepository {
    suspend fun fetchPublicRepositories(userName: String): Result<List<UserRepositoryInfo>>
}

class UserRepositoryImpl @Inject constructor(
    private val repositoryApi: RepositoryApi
) : UserRepository {

    override suspend fun fetchPublicRepositories(userName: String): Result<List<UserRepositoryInfo>> {
        return runCatching {
            repositoryApi.fetchUserRepositories(userName)
        }
            .map { data -> data.map { it.asDomainModel() } }
            .throwCancellation()
    }
}