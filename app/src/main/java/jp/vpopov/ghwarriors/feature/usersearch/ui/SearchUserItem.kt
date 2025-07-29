package jp.vpopov.ghwarriors.feature.usersearch.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import jp.vpopov.ghwarriors.core.designsystem.theme.GHWarriorsTheme
import jp.vpopov.ghwarriors.core.domain.model.User

@Composable
fun SearchUserItem(
    user: User,
    modifier: Modifier = Modifier,
    onItemClick: () -> Unit = {}
) {
    Card(
        onClick = onItemClick,
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            val context = LocalContext.current
            val request = remember(user.avatarUrl) {
                ImageRequest.Builder(context)
                    .data(user.avatarUrl)
                    .crossfade(true)
                    .build()
            }
            AsyncImage(
                model = request,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                error = ColorPainter(Color.LightGray),
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
            Spacer(Modifier.size(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.userName,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchUserItemPreview() {
    GHWarriorsTheme {
        LazyColumn(
            Modifier
                .fillMaxWidth()
                .background(Color.Cyan)
                .padding(24.dp)
        ) {
            items(
                count = 3,
                key = { it }
            ) {
                SearchUserItem(
                    user = User(
                        id = it,
                        userName = "John_Snow_$it",
                        avatarUrl = "https://example.com/avatar.jpg",
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 60.dp)
                )
                Spacer(Modifier.height(4.dp))
            }
        }
    }
}