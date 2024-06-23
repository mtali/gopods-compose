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
package com.colisa.podplay.feaure.podcasts

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.colisa.podplay.R
import com.colisa.podplay.core.designsystem.components.TopBarTitle
import com.colisa.podplay.core.models.Podcast
import com.colisa.podplay.core.utils.display
import com.colisa.podplay.core.utils.toast

@Composable
fun PodcastsRoute(
  viewModel: PodcastsViewModel = hiltViewModel(),
  onClickAbout: () -> Unit,
  onClickSettings: () -> Unit,
  onSelectPodcast: (String) -> Unit,
) {
  val context = LocalContext.current
  viewModel.toastHandler = { context.toast(it) }

  val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val showMode by viewModel.showMode.collectAsStateWithLifecycle()

  PodcastsScreen(
    searchQuery = searchQuery,
    onClickSettings = onClickSettings,
    onClickAbout = onClickAbout,
    onSearchQueryChange = viewModel::onSearchQueryChange,
    onSearch = viewModel::onSearch,
    onSearchActivated = viewModel::onSearchActivated,
    showMode = showMode,
    uiState = uiState,
    onSelectPodcast = onSelectPodcast,
  )
}

@Composable
private fun PodcastsScreen(
  searchQuery: String,
  onClickSettings: () -> Unit,
  onClickAbout: () -> Unit,
  onSearchQueryChange: (String) -> Unit,
  onSearch: () -> Unit,
  showMode: ShowMode,
  onSearchActivated: (Boolean) -> Unit,
  onSelectPodcast: (String) -> Unit,
  uiState: PodcastsUiState,
) {
  Column(modifier = Modifier.fillMaxSize()) {
    GoSearchTopBar(
      modifier = Modifier.fillMaxWidth(),
      searchQuery = searchQuery,
      onClickAbout = onClickAbout,
      onClickSettings = onClickSettings,
      onSearchQueryChange = onSearchQueryChange,
      onSearchActivated = onSearchActivated,
      searchActivated = showMode == ShowMode.SEARCH,
      onSearch = onSearch,
    )

    LinearLoading(visible = uiState.showLoading())

    LazyColumn(modifier = Modifier.weight(1f)) {
      when (uiState) {
        PodcastsUiState.Error -> Unit
        PodcastsUiState.Loading -> Unit
        is PodcastsUiState.Success -> {
          items(uiState.podcasts, key = { it.collectionId }) { podcast ->
            PodcastListItem(podcast = podcast, onClick = { onSelectPodcast(podcast.feedUrl) })
          }
        }
      }
    }
  }
}

@Composable
private fun LinearLoading(visible: Boolean, modifier: Modifier = Modifier) {
  Box(modifier = modifier) {
    AnimatedVisibility(
      visible = visible,
      enter = slideInVertically(),
      exit = slideOutVertically(),
    ) {
      LinearProgressIndicator(Modifier.fillMaxWidth())
    }
  }
}

@Composable
private fun PodcastListItem(podcast: Podcast, onClick: () -> Unit) {
  ListItem(
    headlineContent = {
      Text(text = podcast.feedTitle)
    },
    supportingContent = {
      Text(text = podcast.releaseDate.display())
    },
    leadingContent = {
      AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
          .data(podcast.imageUrl)
          .crossfade(true)
          .build(),
        contentDescription = podcast.feedTitle,
        contentScale = ContentScale.Crop,
        modifier = Modifier
          .size(60.dp)
          .clip(RoundedCornerShape(8.dp)),
      )
    },
    modifier = Modifier.clickable { onClick() },
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GoSearchTopBar(
  onClickAbout: () -> Unit,
  onClickSettings: () -> Unit,
  onSearchQueryChange: (String) -> Unit,
  searchQuery: String,
  onSearch: () -> Unit,
  searchActivated: Boolean,
  onSearchActivated: (Boolean) -> Unit,
  modifier: Modifier = Modifier,
) {
  val keyboardController = LocalSoftwareKeyboardController.current
  val focusRequester = remember { FocusRequester() }
  var showOptions by remember { mutableStateOf(false) }
  var shouldRequestFocus by rememberSaveable { mutableStateOf(true) }

  TopAppBar(
    title = { },
    windowInsets = WindowInsets(top = 0.dp),
    actions = {
      Row(
        modifier = modifier
          .weight(1f)
          .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        if (!searchActivated) {
          TopBarTitle(text = stringResource(id = R.string.app_name), modifier = Modifier.padding(start = 16.dp))
        }

        if (!searchActivated) {
          Spacer(modifier = Modifier.weight(1f))
        } else {
          IconButton(onClick = {
            onSearchActivated(false)
          }) {
            Icon(
              imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
              contentDescription = stringResource(id = R.string.back),
            )
          }

          DisposableEffect(Unit) {
            if (shouldRequestFocus) {
              focusRequester.requestFocus()
            }
            onDispose {
              keyboardController?.hide()
              shouldRequestFocus = false
            }
          }

          OutlinedTextField(
            value = TextFieldValue(text = searchQuery, selection = TextRange(searchQuery.length)),
            onValueChange = {
              onSearchQueryChange(it.text)
            },
            modifier = Modifier
              .focusRequester(focusRequester)
              .weight(1f),
            colors = OutlinedTextFieldDefaults.colors(
              errorBorderColor = Color.Transparent,
              disabledBorderColor = Color.Transparent,
              focusedBorderColor = Color.Transparent,
              unfocusedBorderColor = Color.Transparent,
            ),
            placeholder = {
              Text(text = stringResource(id = R.string.search), fontSize = 18.sp)
            },
            maxLines = 1,
            trailingIcon = {
              if (searchQuery.isNotBlank()) {
                IconButton(onClick = {
                  onSearchQueryChange("")
                  focusRequester.requestFocus()
                }) {
                  Icon(imageVector = Icons.Outlined.Close, contentDescription = stringResource(id = R.string.close))
                }
              }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
              onSearch = {
                if (searchQuery.isNotBlank()) {
                  keyboardController?.hide()
                  onSearch()
                }
              },
            ),
          )

          BackHandler {
            keyboardController?.hide()
            onSearchActivated(false)
          }
        }

        if (!searchActivated) {
          IconButton(onClick = {
            shouldRequestFocus = true
            onSearchActivated(true)
          }) {
            Icon(
              imageVector = Icons.Outlined.Search,
              contentDescription = stringResource(id = R.string.search),
            )
          }
        }

        Box {
          IconButton(onClick = { showOptions = true }) {
            Icon(
              imageVector = Icons.Outlined.MoreVert,
              contentDescription = stringResource(id = R.string.options),
            )
          }
          DropdownMenu(
            expanded = showOptions,
            onDismissRequest = { showOptions = false },
          ) {
            DropdownMenuItem(
              text = { Text(text = stringResource(id = R.string.about)) },
              onClick = {
                showOptions = false
                onClickAbout()
              },
            )
            DropdownMenuItem(
              text = { Text(text = stringResource(id = R.string.settings)) },
              onClick = {
                showOptions = false
                onClickSettings()
              },
            )
          }
        }
      }
    },
  )
}
