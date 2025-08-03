package jp.vpopov.ghwarriors.feature.shared.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import jp.vpopov.ghwarriors.core.extension.Localization

@Composable
fun SwipeToDeleteBox(
    modifier: Modifier = Modifier,
    onItemClicked: () -> Unit = {},
    onItemDelete: () -> Unit = {},
    content: @Composable () -> Unit
) {
    val haptics = LocalHapticFeedback.current
    val deleteProgress = 0.25f
    var currentProgress by remember { mutableFloatStateOf(0f) }
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart && currentProgress >= deleteProgress) {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                onItemDelete()
            }
            false
        },
    )
    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            currentProgress = dismissState.progress
            when (dismissState.dismissDirection) {
                SwipeToDismissBoxValue.StartToEnd -> Unit
                SwipeToDismissBoxValue.Settled -> Unit
                SwipeToDismissBoxValue.EndToStart -> {
                    if (currentProgress > 0) {
                        val offset = with(LocalDensity.current) {
                            dismissState.requireOffset().toDp()
                        }
                        Box(
                            modifier = modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.CenterEnd,
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .widthIn(max = 120.dp)
                                    .width(-offset)
                                    .background(MaterialTheme.colorScheme.errorContainer),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .width(120.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Delete,
                                        tint = MaterialTheme.colorScheme.onErrorContainer,
                                        contentDescription = stringResource(Localization.delete)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        enableDismissFromEndToStart = true,
        enableDismissFromStartToEnd = false,
        modifier = modifier
            .fillMaxSize()
            .combinedClickable(
                onClick = { onItemClicked() },
            ),
        content = { content() },
    )
}