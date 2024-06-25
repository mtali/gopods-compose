/*
 * Designed and developed by 2024 mtali (Emmanuel Mtali)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.colisa.podplay.feaure.podcast

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseOutExpo
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.PlayCircleFilled
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.colisa.podplay.R
import com.colisa.podplay.core.designsystem.components.LinearLoading
import com.colisa.podplay.core.designsystem.components.PodcastImage
import com.colisa.podplay.core.designsystem.components.fullWidthItem
import com.colisa.podplay.core.models.Episode
import com.colisa.podplay.core.models.Podcast
import com.colisa.podplay.core.models.ToastMessage
import com.colisa.podplay.core.utils.toast

@Composable
fun PodcastDetailRoute(viewModel: PodcastDetailViewModel = hiltViewModel(), onBackClick: () -> Unit) {
  val context = LocalContext.current
  viewModel.toastHandler = { context.toast(it) }

  val podcast by viewModel.podcastSteam.collectAsStateWithLifecycle()
  val episodes = viewModel.episodesPagingData.collectAsLazyPagingItems()

  LaunchedEffect(episodes.loadState) {
    if (episodes.loadState.refresh is LoadState.Error) {
      viewModel.toastHandler?.invoke(ToastMessage.SERVICE_ERROR)
    }
  }

  PodcastDetailScreen(
    podcast = podcast,
    onBackClick = onBackClick,
    onToggleSubscription = viewModel::onToggleSubscription,
    episodes = episodes,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PodcastDetailScreen(
  podcast: Podcast?,
  onBackClick: () -> Unit,
  onToggleSubscription: (Long) -> Unit,
  episodes: LazyPagingItems<Episode>,
) {
  Scaffold(
    topBar = {
      TopAppBar(
        title = {},
        navigationIcon = {
          IconButton(onClick = onBackClick) {
            Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = stringResource(id = R.string.back))
          }
        },
        windowInsets = WindowInsets(top = 0.dp),
      )
    },
  ) { innerPadding ->

    Column(
      modifier = Modifier
        .padding(innerPadding)
        .fillMaxSize(),
    ) {
      LinearLoading(visible = episodes.loadState.refresh is LoadState.Loading && episodes.itemCount == 0)

      LazyVerticalGrid(
        columns = GridCells.Adaptive(362.dp),
        Modifier.weight(1f),
      ) {
        if (podcast != null) {
          fullWidthItem {
            PodcastDetailsHeaderItem(
              podcast = podcast,
              onToggleSubscription = onToggleSubscription,
              modifier = Modifier.fillMaxWidth(),
            )
          }

          items(
            count = episodes.itemCount,
            key = episodes.itemKey { it.guid },
          ) { index ->
            val episode = episodes[index]
            if (episode != null) {
              EpisodeListItem(
                episode = episode,
                onClick = { },
                modifier = Modifier.fillMaxWidth(),
                imageUrl = podcast.imageUrl,
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun PodcastDetailsHeaderItem(
  podcast: Podcast,
  onToggleSubscription: (Long) -> Unit,
  modifier: Modifier = Modifier,
) {
  BoxWithConstraints(modifier = modifier.padding(16.dp)) {
    val maxImageSize = this.maxWidth / 2
    val imageSize = min(maxImageSize, 150.dp)
    Column {
      Row(
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier.fillMaxWidth(),
      ) {
        PodcastImage(
          imageUrl = podcast.imageUrl600,
          contentDescription = null,
          modifier = Modifier
            .size(imageSize)
            .clip(RoundedCornerShape(8.dp)),
        )

        Column(modifier = Modifier.padding(start = 16.dp)) {
          Text(
            text = podcast.feedTitle,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.headlineMedium,
          )
          Spacer(modifier = Modifier.height(8.dp))
          SubscribeButton(isSubscribed = podcast.subscribed, onClick = { onToggleSubscription(podcast.id) })
        }
      }

      PodcastDetailsDescription(
        description = podcast.feedDescription,
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 16.dp),
      )
    }
  }
}

@Composable
fun PodcastDetailsDescription(
  description: String,
  modifier: Modifier,
) {
  var isExpanded by remember { mutableStateOf(false) }
  var showSeeMore by remember { mutableStateOf(false) }
  val indicationSource = remember { MutableInteractionSource() }

  Box(
    modifier = modifier.clickable(
      interactionSource = indicationSource,
      indication = null,
      onClick = { isExpanded = !isExpanded },
    ),
  ) {
    Text(
      text = description,
      style = MaterialTheme.typography.bodyMedium,
      maxLines = if (isExpanded) Int.MAX_VALUE else 3,
      overflow = TextOverflow.Ellipsis,
      onTextLayout = { result ->
        showSeeMore = result.hasVisualOverflow
      },
      modifier = Modifier.animateContentSize(
        animationSpec = tween(
          durationMillis = 200,
          easing = EaseOutExpo,
        ),
      ),
    )
    if (showSeeMore) {
      Box(
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .background(MaterialTheme.colorScheme.surface),
      ) {
        Text(
          text = stringResource(id = R.string.see_more),
          style = MaterialTheme.typography.bodyMedium.copy(
            textDecoration = TextDecoration.Underline,
            fontWeight = FontWeight.Bold,
          ),
          modifier = Modifier.padding(start = 16.dp),
        )
      }
    }
  }
}

@Composable
private fun SubscribeButton(
  isSubscribed: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Button(
    onClick = onClick,
    colors = ButtonDefaults.buttonColors(
      containerColor = if (isSubscribed) {
        MaterialTheme.colorScheme.tertiary
      } else {
        MaterialTheme.colorScheme.secondary
      },
    ),
    modifier = modifier.semantics(mergeDescendants = true) { },
  ) {
    Icon(
      imageVector = if (isSubscribed) {
        Icons.Default.Check
      } else {
        Icons.Default.Add
      },
      contentDescription = null,
    )
    Text(
      text = if (isSubscribed) {
        stringResource(id = R.string.subscribed)
      } else {
        stringResource(id = R.string.subscribe)
      },
      modifier = Modifier.padding(start = 8.dp),
    )
  }
}

@Composable
private fun EpisodeListItem(
  episode: Episode,
  imageUrl: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  ListItem(
    headlineContent = {
      Text(text = episode.title)
    },
    leadingContent = {
      PodcastImage(
        imageUrl = imageUrl,
        contentDescription = null,
        modifier = Modifier
          .size(50.dp)
          .clip(RoundedCornerShape(8.dp)),
      )
    },
    trailingContent = {
      Image(
        imageVector = Icons.Rounded.PlayCircleFilled,
        contentDescription = stringResource(R.string.play),
        contentScale = ContentScale.Fit,
        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
        modifier = Modifier
          .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = rememberRipple(bounded = false, radius = 24.dp),
          ) { /* TODO */ }
          .size(48.dp)
          .padding(6.dp)
          .semantics { role = Role.Button },
      )
    },
  )
}
