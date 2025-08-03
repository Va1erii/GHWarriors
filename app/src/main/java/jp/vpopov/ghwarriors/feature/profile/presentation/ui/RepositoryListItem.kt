package jp.vpopov.ghwarriors.feature.profile.presentation.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import jp.vpopov.ghwarriors.core.designsystem.theme.GHWarriorsTheme
import jp.vpopov.ghwarriors.core.domain.model.UserRepoInfo
import jp.vpopov.ghwarriors.feature.shared.presentation.ui.UserRepoInfoPreviewParameterProvider

@Composable
fun RepositoryListItem(
    repository: UserRepoInfo,
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .heightIn(min = 80.dp),
        onClick = onItemClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
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
@Preview
private fun RepositoryListItemPreview(
    @PreviewParameter(UserRepoInfoPreviewParameterProvider::class) repositories: List<UserRepoInfo>,
) {
    GHWarriorsTheme {
        Surface {
            LazyColumn {
                items(
                    count = repositories.size,
                    key = { repositories[it].id }
                ) {
                    RepositoryListItem(
                        repository = repositories[it],
                        onItemClick = {},
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}