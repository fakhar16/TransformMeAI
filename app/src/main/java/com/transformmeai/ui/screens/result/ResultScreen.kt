package com.transformmeai.ui.screens.result

import android.content.Intent
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.transformmeai.R
import com.transformmeai.domain.model.LoadableUiState
import com.transformmeai.ui.components.PrimaryButton
import com.transformmeai.viewmodel.ResultViewModel

private val lookLabels =
    listOf(
        R.string.style_free,
        R.string.style_hair,
        R.string.style_outfit,
        R.string.style_anime,
        R.string.style_editorial,
    )

@Composable
fun ResultRoute(
    onLockedLookTapped: () -> Unit,
    onStartOver: () -> Unit,
    viewModel: ResultViewModel = hiltViewModel(),
) {
    val session by viewModel.sessionState.collectAsStateWithLifecycle()
    val downloadState by viewModel.downloadState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(downloadState) {
        when (val state = downloadState) {
            is LoadableUiState.Success -> {
                snackbarHostState.showSnackbar(context.getString(R.string.saved_to_pictures))
                viewModel.consumeDownloadMessage()
            }
            is LoadableUiState.Error -> {
                snackbarHostState.showSnackbar(state.message)
                viewModel.consumeDownloadMessage()
            }
            else -> Unit
        }
    }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
        ) {
            Text(
                text = stringResource(R.string.your_looks),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.tagline),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (session.generatedLookUrls.size == 5) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 96.dp),
                ) {
                    itemsIndexed(session.generatedLookUrls) { index, url ->
                        val unlocked = index == 0 || session.allLooksUnlocked
                        LookTile(
                            title = stringResource(lookLabels[index]),
                            imageUrl = url,
                            locked = !unlocked,
                            onLockedClick = onLockedLookTapped,
                            onDownload = {
                                if (unlocked) {
                                    viewModel.downloadUnlockedLook(url)
                                }
                            },
                            onShare = {
                                if (unlocked) {
                                    val send =
                                        Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(Intent.EXTRA_TEXT, url)
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        }
                                    context.startActivity(
                                        Intent.createChooser(send, context.getString(R.string.share)),
                                    )
                                }
                            },
                            downloadInFlight = downloadState is LoadableUiState.Loading,
                        )
                    }
                }
            } else {
                Text(
                    text = stringResource(R.string.error_generic),
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }

        Column(
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(20.dp),
        ) {
            OutlinedButton(
                onClick = {
                    viewModel.startNewSession()
                    onStartOver()
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = stringResource(R.string.take_another_photo))
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 96.dp),
        )
    }
}

@Composable
private fun LookTile(
    title: String,
    imageUrl: String,
    locked: Boolean,
    onLockedClick: () -> Unit,
    onDownload: () -> Unit,
    onShare: () -> Unit,
    downloadInFlight: Boolean,
) {
    val context = LocalContext.current
    val imageRequest =
        remember(imageUrl) {
            ImageRequest
                .Builder(context)
                .data(imageUrl)
                .crossfade(true)
                .build()
        }

    val lockedBlurModifier =
        if (locked && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Modifier.graphicsLayer {
                renderEffect =
                    RenderEffect
                        .createBlurEffect(20f, 20f, Shader.TileMode.CLAMP)
                        .asComposeRenderEffect()
            }
        } else {
            Modifier
        }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .then(
                            if (locked) {
                                Modifier.clickable { onLockedClick() }
                            } else {
                                Modifier
                            },
                        ),
            ) {
                AsyncImage(
                    model = imageRequest,
                    contentDescription = title,
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .then(lockedBlurModifier),
                    contentScale = ContentScale.Crop,
                )
                if (locked) {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .background(
                                    Color.Black.copy(
                                        alpha = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) 0.28f else 0.45f,
                                    ),
                                ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(36.dp),
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (!locked) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PrimaryButton(
                        text = stringResource(R.string.download),
                        onClick = onDownload,
                        modifier = Modifier.weight(1f),
                        enabled = !downloadInFlight,
                        expandWidth = false,
                    )
                    OutlinedButton(
                        onClick = onShare,
                        modifier = Modifier.weight(1f),
                    ) {
                        Icon(Icons.Filled.Share, contentDescription = stringResource(R.string.share))
                    }
                }
            }
        }
    }
}
