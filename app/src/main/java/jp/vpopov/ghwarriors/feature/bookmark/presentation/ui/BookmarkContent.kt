package jp.vpopov.ghwarriors.feature.bookmark.presentation.ui

import android.content.res.Configuration
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jp.vpopov.ghwarriors.R
import jp.vpopov.ghwarriors.core.designsystem.theme.GHWarriorsTheme
import jp.vpopov.ghwarriors.core.domain.model.UserInfo
import jp.vpopov.ghwarriors.core.extension.Localization
import jp.vpopov.ghwarriors.feature.bookmark.presentation.component.BookmarkComponent
import jp.vpopov.ghwarriors.feature.shared.presentation.ui.SwipeToDeleteBox
import jp.vpopov.ghwarriors.feature.shared.presentation.ui.UserItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarkContent(
    component: BookmarkComponent,
    modifier: Modifier = Modifier
) {
    val bookmarks by component.bookmarks.collectAsState(emptyList())
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(Localization.bookmarks),
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )
            },
            modifier = Modifier.fillMaxWidth()
        )
        BookmarkContentSection(
            bookmarks = bookmarks,
            onUserClick = component::onUserSelected,
            onRemoveBookmark = component::onRemoveBookmark,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun BookmarkContentSection(
    bookmarks: List<UserInfo>,
    onUserClick: (UserInfo) -> Unit,
    onRemoveBookmark: (UserInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    when {
        bookmarks.isEmpty() -> EmptyBookmarksContent(modifier = modifier)
        else -> BookmarksList(
            bookmarks = bookmarks,
            onUserClick = onUserClick,
            onRemoveBookmark = onRemoveBookmark,
            modifier = modifier
        )
    }
}

@Composable
private fun BookmarksList(
    bookmarks: List<UserInfo>,
    onUserClick: (UserInfo) -> Unit,
    onRemoveBookmark: (UserInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(
            items = bookmarks,
            key = { it.id }
        ) { user ->
            SwipeToDeleteBox(
                onItemClicked = { onUserClick(user) },
                onItemDelete = { onRemoveBookmark(user) }
            ) {
                UserItem(
                    user = user,
                    onClick = { onUserClick(user) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun EmptyBookmarksContent(
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
                    Image(
                        painter = painterResource(R.drawable.ic_not_found),
                        contentDescription = null,
                        modifier = Modifier.size(128.dp),
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(Localization.bookmarks_empty_title),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(Localization.bookmarks_empty_message),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun EmptyBookmarksContentPreview() {
    GHWarriorsTheme {
        Surface {
            EmptyBookmarksContent()
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun BookmarksListPreview() {
    val sampleBookmarks = listOf(
        UserInfo(
            id = 1,
            userName = "octocat",
            avatarUrl = "https://github.com/octocat.png"
        ),
        UserInfo(
            id = 2,
            userName = "github",
            avatarUrl = "https://github.com/github.png"
        ),
        UserInfo(
            id = 3,
            userName = "torvalds",
            avatarUrl = "https://github.com/torvalds.png"
        )
    )

    GHWarriorsTheme {
        Surface {
            BookmarksList(
                bookmarks = sampleBookmarks,
                onUserClick = {},
                onRemoveBookmark = {}
            )
        }
    }
}