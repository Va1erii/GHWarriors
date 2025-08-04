package jp.vpopov.ghwarriors.feature.profile.domain

import androidx.paging.PagingData
import androidx.paging.filter
import jp.vpopov.ghwarriors.core.data.userrepo.UserRepoInfoRepository
import jp.vpopov.ghwarriors.core.domain.model.UserRepoInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Use case for retrieving non-forked public repositories for a specific user.
 *
 * This use case filters out forked repositories from the user's repository list,
 * returning only original repositories created by the user. This is useful for
 * displaying a user's original work without including repositories they have forked
 * from other users.
 *
 * @param userRepoInfoRepository Repository for fetching user repository information
 */
class GetNonForkedPublicRepositories @Inject constructor(
    private val userRepoInfoRepository: UserRepoInfoRepository
) {
    operator fun invoke(userId: Int): Flow<PagingData<UserRepoInfo>> {
        return userRepoInfoRepository.fetchRepositories(userId)
            .map { data -> data.filter { !it.isFork } }
    }
}