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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.colisa.podplay.R
import com.colisa.podplay.core.designsystem.components.TopBarTitle

@Composable
fun PodcastsRoute(
  viewModel: PodcastsViewModel = hiltViewModel(),
  onClickAbout: () -> Unit,
  onClickSettings: () -> Unit,
) {
  val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

  PodcastsScreen(
    searchQuery = searchQuery,
    onClickSettings = onClickSettings,
    onClickAbout = onClickAbout,
    onSearchQueryChange = viewModel::onSearchQueryChange,
    onSearch = viewModel::onSearch,

    )
}

@Composable
private fun PodcastsScreen(
  searchQuery: String,
  onClickSettings: () -> Unit,
  onClickAbout: () -> Unit,
  onSearchQueryChange: (String) -> Unit,
  onSearch: () -> Unit,
) {
  Column(modifier = Modifier.fillMaxSize()) {
    GoSearchTopBar(
      modifier = Modifier.fillMaxWidth(),
      searchQuery = searchQuery,
      onClickAbout = onClickAbout,
      onClickSettings = onClickSettings,
      onSearchQueryChange = onSearchQueryChange,
      onSearch = onSearch,
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GoSearchTopBar(
  onClickAbout: () -> Unit,
  onClickSettings: () -> Unit,
  onSearchQueryChange: (String) -> Unit,
  searchQuery: String,
  onSearch: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val keyboardController = LocalSoftwareKeyboardController.current
  val focusRequester = remember { FocusRequester() }
  var searchActivated by remember { mutableStateOf(false) }
  var showOptions by remember { mutableStateOf(false) }

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
            searchActivated = false
          }) {
            Icon(
              imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
              contentDescription = stringResource(id = R.string.back),
            )
          }

          DisposableEffect(Unit) {
            focusRequester.requestFocus()
            onDispose {
              keyboardController?.hide()
              onSearchQueryChange("")
            }
          }

          OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
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
                }) {
                  Icon(imageVector = Icons.Outlined.Close, contentDescription = stringResource(id = R.string.close))
                }
              }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
              onSearch = {
                if (searchQuery.isNotBlank()) {
                  onSearch()
                }
              },
            ),
          )

          BackHandler {
            searchActivated = false
          }
        }

        if (!searchActivated) {
          IconButton(onClick = { searchActivated = true }) {
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
