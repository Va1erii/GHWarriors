package jp.vpopov.ghwarriors.feature.profile.presentation.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import coil3.compose.AsyncImage
import jp.vpopov.ghwarriors.R
import jp.vpopov.ghwarriors.core.designsystem.theme.GHWarriorsTheme
import jp.vpopov.ghwarriors.core.domain.model.UserProfileInfo
import jp.vpopov.ghwarriors.core.domain.model.UserRepositoryInfo
import jp.vpopov.ghwarriors.feature.profile.presentation.component.ProfileComponent

@Composable
fun ProfileContent(
    component: ProfileComponent,
    modifier: Modifier = Modifier
) {
    val state by component.model.collectAsState()
    val userProfileInfo = state.userProfileInfo
    if (userProfileInfo != null) {
        ProfileContent(
            profile = userProfileInfo,
            data = state.repositories,
            onBookmarkToggle = component::onUserBookmarkToggle,
            modifier = modifier.fillMaxSize()
        )
    }
}

@Composable
fun ProfileContent(
    profile: UserProfileInfo,
    data: List<UserRepositoryInfo>,
    onBookmarkToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .navigationBarsPadding()
            .statusBarsPadding()
            .padding(start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = profile.avatarUrl,
            contentDescription = stringResource(R.string.user_avatar),
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.tertiary)
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
        Text(
            text = "@${profile.userName}",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        profile.bio?.let { bio ->
            Text(
                text = bio,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
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
                count = profile.totalPublicReposCount,
                label = stringResource(R.string.repositories),
                modifier = Modifier.weight(1f)
            )
        }
        RepositoryList(
            data = data,
            onItemClick = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun RepositoryList(
    data: List<UserRepositoryInfo>,
    onItemClick: (UserRepositoryInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(
            count = data.size,
            key = { index -> data[index].id }
        ) { index ->
            val repository = data[index]
            val click by rememberUpdatedState(onItemClick)
            RepositoryListItem(
                repository = repository,
                onItemClick = { click(repository) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun RepositoryListItem(
    repository: UserRepositoryInfo,
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 80.dp)
    ) {
        Row(
            modifier = modifier
                .padding(16.dp)
                .clickable(onClick = onItemClick),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = repository.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
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
        ProfileContent(
            profile = UserProfileInfo(
                userId = 1,
                name = "John Doe",
                userName = "john_doe",
                avatarUrl = "https://example.com/avatar.jpg",
                bio = "This is a sample bio for John Doe.",
                followersCount = 150,
                followingCount = 75,
                totalPublicReposCount = 10
            ),
            data = listOf(
                UserRepositoryInfo(
                    id = 1,
                    name = "Sample Repository",
                    fullName = "john_doe/sample-repo",
                    description = "This is a sample repository.",
                    url = "",
                    language = "Kotlin",
                    starsCount = 100
                )
            ),
            onBookmarkToggle = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}