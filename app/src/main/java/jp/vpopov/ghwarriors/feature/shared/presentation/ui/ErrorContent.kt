package jp.vpopov.ghwarriors.feature.shared.presentation.ui

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jp.vpopov.ghwarriors.R
import jp.vpopov.ghwarriors.core.designsystem.theme.GHWarriorsTheme
import jp.vpopov.ghwarriors.core.error.AppError
import jp.vpopov.ghwarriors.core.error.NetworkError
import jp.vpopov.ghwarriors.core.error.UnknownError
import jp.vpopov.ghwarriors.core.extension.Localization
import jp.vpopov.ghwarriors.util.PainterResource
import jp.vpopov.ghwarriors.util.painter

@Composable
fun ErrorContent(
    appError: AppError,
    modifier: Modifier = Modifier,
    showRetryButton: Boolean = true,
    onRetry: () -> Unit = {},
) {
    val errorInfo by remember {
        derivedStateOf {
            when (appError) {
                is NetworkError.NoInternetConnection -> ErrorInfo(
                    image = PainterResource.DrawableResource(R.drawable.ic_no_internet),
                    title = Localization.error_no_internet_title,
                    message = Localization.error_no_internet_message,
                )

                is NetworkError.Timeout -> ErrorInfo(
                    image = PainterResource.DrawableResource(R.drawable.ic_no_internet),
                    title = Localization.error_timeout_title,
                    message = Localization.error_timeout_message,
                )

                else -> ErrorInfo(
                    image = PainterResource.DrawableResource(R.drawable.ic_server_error),
                    title = Localization.error_common_title,
                    message = Localization.error_common_message
                )
            }
        }
    }
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.Center
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
                        painter = errorInfo.image.painter(),
                        contentDescription = null,
                        modifier = Modifier.size(128.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(errorInfo.title),
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(errorInfo.message),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    if (showRetryButton) {
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = onRetry
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(Localization.try_again))
                        }
                    }
                }
            }
        }
    }
}

private data class ErrorInfo(
    val image: PainterResource,
    @StringRes val title: Int,
    @StringRes val message: Int
)

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ErrorContentNoInternetPreview() {
    GHWarriorsTheme {
        Surface {
            ErrorContent(
                appError = NetworkError.NoInternetConnection(),
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ErrorContentTimeoutPreview() {
    GHWarriorsTheme {
        Surface {
            ErrorContent(
                appError = NetworkError.Timeout(),
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ErrorContentGenericErrorPreview() {
    GHWarriorsTheme {
        Surface {
            ErrorContent(
                appError = UnknownError(RuntimeException("Something went wrong")),
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ErrorContentNoRetryButtonPreview() {
    MaterialTheme {
        Surface {
            ErrorContent(
                appError = NetworkError.NoInternetConnection(),
                showRetryButton = false,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}