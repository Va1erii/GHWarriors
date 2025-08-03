package jp.vpopov.ghwarriors.feature.profile.presentation.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import jp.vpopov.ghwarriors.R
import jp.vpopov.ghwarriors.core.designsystem.theme.GHWarriorsTheme
import jp.vpopov.ghwarriors.core.domain.model.UserProfileInfo
import jp.vpopov.ghwarriors.core.domain.model.UserRepoInfo
import jp.vpopov.ghwarriors.core.error.AppError
import jp.vpopov.ghwarriors.core.error.ErrorMapper
import jp.vpopov.ghwarriors.core.extension.Localization
import jp.vpopov.ghwarriors.feature.profile.presentation.component.ProfileComponent
import jp.vpopov.ghwarriors.feature.profile.presentation.component.ProfileState
import jp.vpopov.ghwarriors.feature.shared.presentation.ui.ErrorContent
import jp.vpopov.ghwarriors.feature.shared.presentation.ui.LoadMoreError
import jp.vpopov.ghwarriors.feature.shared.presentation.ui.LoadingMore

@Composable
fun ProfileContent(
    component: ProfileComponent,
    modifier: Modifier = Modifier
) {
    val model by component.model.collectAsState()
    val repositories = component.repositories.collectAsLazyPagingItems()
    when (val state = model) {
        is ProfileState.Loading -> LoadingContent(modifier)

        is ProfileState.Error -> ErrorContent(
            appError = state.error,
            onRetry = component::onRefresh,
            modifier = modifier
        )

        is ProfileState.Success -> ProfileContent(
            state = state,
            data = repositories,
            onBookmarkToggle = component::onUserBookmarkToggle,
            onRepositorySelected = component::onRepositorySelected,
            onRepositoryRetry = component::onRefreshRepositories,
            modifier = modifier
        )
    }
}

@Composable
private fun LoadingContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ProfileContent(
    state: ProfileState.Success,
    data: LazyPagingItems<UserRepoInfo>,
    onBookmarkToggle: () -> Unit,
    onRepositorySelected: (UserRepoInfo) -> Unit,
    onRepositoryRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val error by remember(data.loadState.refresh) {
        derivedStateOf {
            val errorState = data.loadState.refresh as? LoadState.Error
            errorState?.let { ErrorMapper.convert(it.error) }
        }
    }
    LazyColumn(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item(
            key = state.userProfileInfo.userName,
            contentType = "header"
        ) {
            ProfileHeaderContent(
                profile = state.userProfileInfo,
                onBookmarkToggle = onBookmarkToggle,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(24.dp))
        }
        repositoryListSection(
            data = data,
            error = error,
            onRepositorySelected = onRepositorySelected,
            onRepositoryRetry = onRepositoryRetry,
        )
    }
}

private fun LazyListScope.repositoryListSection(
    data: LazyPagingItems<UserRepoInfo>,
    error: AppError?,
    onRepositorySelected: (UserRepoInfo) -> Unit,
    onRepositoryRetry: () -> Unit
) {
    val isLoading = data.loadState.refresh is LoadState.Loading
    when {
        isLoading -> items(
            count = 4,
            key = { it },
            contentType = { "loading_item" }
        ) { RepositoryListItemShimmer() }

        error != null -> item(
            key = "error_item",
            contentType = "error_item"
        ) {
            RepositoryListSectionError(onRetry = onRepositoryRetry)
        }

        else -> items(
            count = data.itemCount,
            key = data.itemKey { it.id },
            contentType = { "repository_item" }
        ) { index ->
            val repository = data[index]
            val repositoryClick by rememberUpdatedState(onRepositorySelected)
            if (repository != null) {
                RepositoryListItem(
                    repository = repository,
                    onItemClick = { repositoryClick(repository) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    when (data.loadState.append) {
        is LoadState.Loading -> item(
            key = "loading_more_item",
            contentType = "loading_more_item"
        ) { LoadingMore(modifier = Modifier.fillMaxWidth()) }

        is LoadState.Error -> item(
            key = "load_more_error_item",
            contentType = "load_more_error_item"
        ) { LoadMoreError(onRetry = { data.retry() }) }

        else -> Unit
    }
}

@Composable
private fun RepositoryListSectionError(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = stringResource(Localization.error_common_title),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(Localization.error_common_message),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRetry
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(Localization.try_again))
        }
    }
}

@Composable
private fun ProfileHeaderContent(
    profile: UserProfileInfo,
    onBookmarkToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = profile.avatarUrl,
            contentDescription = stringResource(R.string.user_avatar),
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondary)
                .padding(1.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
            placeholder = ColorPainter(Color.LightGray),
            error = ColorPainter(Color.LightGray)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = profile.name ?: profile.userName,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        val userName by remember(profile.userName) {
            derivedStateOf {
                "@${profile.userName}"
            }
        }
        Text(
            text = userName,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        profile.bio?.let { bio ->
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = bio,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                count = profile.followersCount,
                label = stringResource(R.string.followers),
                modifier = Modifier.weight(1f)
            )

            StatCard(
                count = profile.followingCount,
                label = stringResource(R.string.following),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                count = profile.publicReposCount,
                label = stringResource(R.string.repositories),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatCard(
    count: Int,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showSystemUi = true)
@Preview(
    showSystemUi = true,
    device = Devices.TABLET,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun ProfileContentPreview() {
    GHWarriorsTheme {
        Scaffold { innerPadding ->
            ProfileHeaderContent(
                profile = UserProfileInfo(
                    userId = 1,
                    name = "John Doe",
                    userName = "john_doe",
                    avatarUrl = "https://example.com/avatar.jpg",
                    bio = "This is a sample bio for John Doe.",
                    followersCount = 150,
                    followingCount = 75,
                    publicReposCount = 10
                ),
                onBookmarkToggle = {},
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxWidth()
            )
        }
    }
}