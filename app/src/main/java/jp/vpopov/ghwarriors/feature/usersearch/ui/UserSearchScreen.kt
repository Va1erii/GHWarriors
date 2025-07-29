package jp.vpopov.ghwarriors.feature.usersearch.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import jp.vpopov.ghwarriors.core.domain.model.User
import kotlinx.coroutines.flow.Flow

@Composable
fun UserSearchScreen(
    onItemClick: (User) -> Unit,
    viewModel: UserSearchViewModel = viewModel(),
) {
    val pagingData: Flow<PagingData<User>> = viewModel.users
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        UserSearchContent(
            pagingData = pagingData,
            innerPadding = innerPadding,
            onItemClick = onItemClick,
            modifier = Modifier.consumeWindowInsets(innerPadding)
        )
    }
}

@Composable
private fun UserSearchContent(
    pagingData: Flow<PagingData<User>>,
    innerPadding: PaddingValues,
    onItemClick: (User) -> Unit,
    modifier: Modifier = Modifier
) {
    val data = pagingData.collectAsLazyPagingItems()
    LazyColumn(
        modifier = modifier,
        contentPadding = innerPadding
    ) {
        items(
            count = data.itemCount,
            key = data.itemKey { it.id },
        ) { index ->
            val user = data[index]
            if (user != null) {
                val currentOnItemClick by rememberUpdatedState(onItemClick)
                SearchUserItem(
                    user = user,
                    onItemClick = { currentOnItemClick(user) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp)
                )
            }
        }
    }
}

