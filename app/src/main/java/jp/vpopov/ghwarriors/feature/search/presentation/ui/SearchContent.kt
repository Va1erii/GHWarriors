package jp.vpopov.ghwarriors.feature.search.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import jp.vpopov.ghwarriors.R
import jp.vpopov.ghwarriors.core.error.ErrorMapper
import jp.vpopov.ghwarriors.core.extension.Localization
import jp.vpopov.ghwarriors.feature.search.presentation.component.SearchComponent
import jp.vpopov.ghwarriors.feature.search.presentation.component.SearchComponent.UserWithBookmarkInfo
import jp.vpopov.ghwarriors.feature.shared.presentation.ui.ErrorContent
import jp.vpopov.ghwarriors.feature.shared.presentation.ui.LoadMoreError
import jp.vpopov.ghwarriors.feature.shared.presentation.ui.LoadingMore
import jp.vpopov.ghwarriors.feature.shared.presentation.ui.UserItem
import jp.vpopov.ghwarriors.feature.shared.presentation.ui.UserItemShimmer
import kotlinx.coroutines.delay


@Composable
fun SearchContent(
    component: SearchComponent,
    modifier: Modifier = Modifier
) {
    val query by component.query.collectAsState("")
    val users = component.users.collectAsLazyPagingItems()
    var searchQuery by remember { mutableStateOf(query) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
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
            onBookmarkClick = { user -> component.onBookmarkClick(user) },
            modifier = Modifier.weight(1f)
        )
    }
}


@Composable
private fun SearchContentSection(
    query: String,
    data: LazyPagingItems<UserWithBookmarkInfo>,
    onUserClick: (UserWithBookmarkInfo) -> Unit,
    onBookmarkClick: (UserWithBookmarkInfo) -> Unit,
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

        isIdle && query.isEmpty() -> EmptyQueryContent(modifier = modifier)

        isIdle && data.itemCount == 0 && query.isNotEmpty() -> NoDataFoundContent(
            query, modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        )

        else -> PagingSearchResultsList(
            data = data,
            onUserClick = onUserClick,
            onBookmarkClick = onBookmarkClick,
            modifier = modifier
        )
    }
}

@Composable
private fun EmptyQueryContent(
    modifier: Modifier = Modifier
) {
    LazyColumn(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        item {
            Box(
                modifier = Modifier.fillMaxSize(),
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
                        text = stringResource(Localization.search_onboarding_title),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(Localization.search_onboarding_message),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun PagingSearchResultsList(
    data: LazyPagingItems<UserWithBookmarkInfo>,
    onUserClick: (UserWithBookmarkInfo) -> Unit,
    onBookmarkClick: (UserWithBookmarkInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(
            count = data.itemCount,
            key = { index -> data[index]?.user?.id ?: index }
        ) { index ->
            val item = data[index]
            if (item != null) {
                UserItem(
                    user = item.user,
                    onClick = { onUserClick(item) },
                    showBookmark = true,
                    isBookmarked = item.isBookmarked,
                    onBookmarkClick = { onBookmarkClick(item) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        // Handle loading more paging states
        when (data.loadState.append) {
            is LoadState.Loading -> item { LoadingMore(modifier = Modifier.fillMaxWidth()) }
            is LoadState.Error -> item { LoadMoreError(onRetry = { data.retry() }) }
            else -> Unit
        }
    }
}

@Composable
private fun NoDataFoundContent(
    query: String,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_not_found),
                        contentDescription = null,
                        modifier = Modifier.size(128.dp),
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(Localization.search_no_users_found_title),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(Localization.search_no_users_found_message, query),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
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
            UserItemShimmer()
        }
    }
}