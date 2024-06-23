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

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.colisa.podplay.core.data.repositories.PodcastsRepo
import com.colisa.podplay.core.network.utils.Resource
import com.colisa.podplay.feaure.podcast.navigation.PodcastDetailArg
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PodcastDetailViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle,
  podcastsRepo: PodcastsRepo,
) : ViewModel() {

  private val args = PodcastDetailArg(savedStateHandle)

  val uiState = podcastsRepo.getPodcastFeed(feedUrl = args.feedUrl)
    .map { result ->
      when (result) {
        is Resource.Error -> {
          Timber.d("Error: ${result.error} ${result.data}")
        }

        is Resource.Loading -> {
          Timber.d("Loading: ${result.error} ${result.data}")
        }

        is Resource.Success -> {
          Timber.d("Success: ${result.error} ${result.data}")
        }
      }
    }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000),
      initialValue = null,
    )

  init {
    Timber.d("Podcast feed: ${args.feedUrl}")
  }
}
