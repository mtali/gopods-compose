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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.colisa.podplay.core.designsystem.components.LinearLoading
import com.colisa.podplay.core.designsystem.components.TopBarTitle
import com.colisa.podplay.core.designsystem.components.fullWidthItem
import com.colisa.podplay.core.models.Episode
import com.colisa.podplay.core.models.Podcast
import com.colisa.podplay.core.utils.toast

@Composable
fun PodcastDetailRoute(viewModel: PodcastDetailViewModel = hiltViewModel()) {
  val context = LocalContext.current
  viewModel.toastHandler = { context.toast(it) }

  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  PodcastDetailScreen(uiState = uiState)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PodcastDetailScreen(
  uiState: PodcastDetailUiState,
) {
  Scaffold(
    topBar = {
      TopAppBar(
        title = { TopBarTitle(text = "Podcast") },
        windowInsets = WindowInsets(top = 0.dp),
      )
    },
  ) { innerPadding ->

    Column(
      modifier = Modifier
        .padding(innerPadding)
        .fillMaxSize(),
    ) {
      LinearLoading(visible = uiState.isLoading)

      LazyVerticalGrid(
        columns = GridCells.Adaptive(362.dp),
        Modifier.weight(1f),
      ) {
        val podcast = uiState.podcast
        if (podcast != null) {
          fullWidthItem {
            PodcastDetailsHeaderItem(
              podcast = podcast,
              onToggleSubscription = {},
              modifier = Modifier.fillMaxWidth(),
            )
          }

          items(podcast.episodes, key = { it.guid }) { episode ->
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

@Composable
private fun PodcastDetailsHeaderItem(
  podcast: Podcast,
  onToggleSubscription: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(modifier = modifier) {
    Text(text = podcast.feedTitle)
    Text(text = podcast.imageUrl600)
    Text(text = "Subscribe")
    Text(text = podcast.feedDescription)
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
      AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
          .data(imageUrl)
          .crossfade(true)
          .build(),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = Modifier
          .size(50.dp)
          .clip(RoundedCornerShape(8.dp)),
      )
    },
    trailingContent = {
    },
  )
}
