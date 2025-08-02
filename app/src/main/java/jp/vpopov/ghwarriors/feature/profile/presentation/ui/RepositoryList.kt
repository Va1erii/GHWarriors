package jp.vpopov.ghwarriors.feature.profile.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import jp.vpopov.ghwarriors.core.domain.model.UserRepoInfo

@Composable
fun RepositoryList(
    data: List<UserRepoInfo>,
    onItemClick: (UserRepoInfo) -> Unit,
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
    repository: UserRepoInfo,
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