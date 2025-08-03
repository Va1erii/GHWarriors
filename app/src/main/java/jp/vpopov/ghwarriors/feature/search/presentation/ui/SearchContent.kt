package jp.vpopov.ghwarriors.feature.search.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import jp.vpopov.ghwarriors.core.domain.model.UserInfo
import jp.vpopov.ghwarriors.core.error.ErrorMapper
import jp.vpopov.ghwarriors.feature.search.presentation.component.SearchComponent
import jp.vpopov.ghwarriors.feature.shared.presentation.ui.ErrorContent
import kotlinx.coroutines.delay


@Composable
fun SearchContent(
    component: SearchComponent,
    modifier: Modifier = Modifier
) {
    val query by component.query.collectAsState("")
    val users = component.users.collectAsLazyPagingItems()
    var searchQuery by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(query) {
        // Sync state with UI if necessary
        if (searchQuery != query) {
            searchQuery = query
        }
    }

    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotEmpty()) {
            delay(300)
            component.search(searchQuery)
        } else {
            component.search("")
        }
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        SearchInputSection(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            focusRequester = focusRequester,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp)
        )
        SearchContentSection(
            query = query,
            data = users,
            onUserClick = { user -> component.onUserSelected(user) },
            modifier = Modifier.weight(1f)
        )
    }
}


@Composable
private fun SearchContentSection(
    query: String,
    data: LazyPagingItems<UserInfo>,
    onUserClick: (UserInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    val isLoading = data.loadState.refresh is LoadState.Loading
    val isIdle = data.loadState.isIdle
    val error by remember(data.loadState.refresh) {
        derivedStateOf {
            val errorState = data.loadState.refresh as? LoadState.Error
            errorState?.let { ErrorMapper.convert(it.error) }
        }
    }
    when {
        isLoading -> LoadingContent(modifier = modifier)

        error != null -> error?.let {
            ErrorContent(
                appError = it,
                onRetry = { data.retry() },
                modifier = modifier
            )
        }

        isIdle && query.isEmpty() -> EmptyQueryContent()

        isIdle && data.itemCount == 0 && query.isNotEmpty() -> NoDataFoundContent(
            query, modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        )

        else -> PagingSearchResultsList(
            data = data,
            onUserClick = onUserClick,
            modifier = modifier
        )
    }
}

@Composable
private fun EmptyQueryContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Start typing to search",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Search for users by name, username, or bio",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun PagingSearchResultsList(
    data: LazyPagingItems<UserInfo>,
    onUserClick: (UserInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(
            count = data.itemCount,
            key = { index -> data[index]?.id ?: index }
        ) { index ->
            val user = data[index]
            if (user != null) {
                SearchUserItem(
                    user = user,
                    onClick = { onUserClick(user) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp)
                )
            }
        }
        // Handle paging states
        when (val loadState = data.loadState.append) {
            is LoadState.Loading -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                }
            }

            is LoadState.Error -> {
                item {
                    LoadMoreErrorContent(onRetry = { data.retry() })
                }
            }

            else -> {}
        }
    }
}

@Composable
private fun NoDataFoundContent(
    query: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No users found",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "No users found for \"$query\". Try searching with different keywords.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}


@Composable
private fun LoadMoreErrorContent(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Failed to load more",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(
            onClick = onRetry
        ) {
            Text("Retry")
        }
    }
}

@Composable
private fun LoadingContent(
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        contentPadding = PaddingValues(vertical = 8.dp),
        modifier = modifier
    ) {
        items(5) {
            SearchUserItemShimmer()
        }
    }
}