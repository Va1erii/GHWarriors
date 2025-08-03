package jp.vpopov.ghwarriors.feature.shared.presentation.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import jp.vpopov.ghwarriors.core.domain.model.UserInfo

class UserInfoPreviewParameterProvider : PreviewParameterProvider<List<UserInfo>> {
    override val values = sequenceOf(
        listOf(
            UserInfo(
                id = 1,
                userName = "octocat",
                avatarUrl = "https://github.com/octocat.png"
            ),
            UserInfo(
                id = 2,
                userName = "torvalds",
                avatarUrl = "https://github.com/torvalds.png"
            ),
            UserInfo(
                id = 3,
                userName = "very-long-username-that-might-wrap",
                avatarUrl = "https://github.com/verylongusername.png"
            ),
            UserInfo(
                id = 4,
                userName = "user",
                avatarUrl = ""
            ),
            UserInfo(
                id = 5,
                userName = "super long username that lore like ipsum and why I am doing it?",
                avatarUrl = ""
            )
        )
    )
}