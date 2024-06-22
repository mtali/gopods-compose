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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.colisa.podplay.core.data.repositories.PodcastsRepo
import com.colisa.podplay.core.models.Podcast
import com.colisa.podplay.core.models.ToastMessage
import com.colisa.podplay.core.network.utils.Resource
import com.colisa.podplay.core.utils.isRunning
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PodcastsViewModel @Inject constructor(
  private val podcastsRepo: PodcastsRepo,
) : ViewModel() {
  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery

  private val _subscribedPodcasts = podcastsRepo.getPodcasts(subscribed = true)
  private val _podcastsSearchResult = MutableStateFlow<List<Podcast>>(emptyList())
  private val _isLoading = MutableStateFlow(false)

  private val _mode = MutableStateFlow(UiMode.SUBSCRIBED)
  val mode: StateFlow<UiMode> = _mode

  val uiState = combine(
    _mode,
    _isLoading,
    _subscribedPodcasts,
    _podcastsSearchResult,
  ) { uiMode, isLoading, subscribedPodcasts, podcastSearchResult ->
    PodcastsUiState.Success(
      isLoading = isLoading,
      mode = uiMode,
      podcasts = when (uiMode) {
        UiMode.SEARCH -> podcastSearchResult
        UiMode.SUBSCRIBED -> subscribedPodcasts
      },
    )
  }
    .catch { PodcastsUiState.Error }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000),
      initialValue = PodcastsUiState.Loading,
    )

  var toastHandler: ((ToastMessage) -> Unit)? = null

  private var searchJob: Job? = null

  fun onSearchQueryChange(value: String) {
    if (value.trim() == "" && searchJob.isRunning()) {
      searchJob?.cancel()
      _podcastsSearchResult.update { emptyList() }
    }
    _searchQuery.update { value }
  }

  fun onSearch() {
    val query = searchQuery.value.trim().lowercase()
    if (query.isBlank()) return
    searchJob?.cancel()
    searchJob = viewModelScope.launch {
      podcastsRepo.searchPodcasts(term = query)
        .collectLatest { response ->
          val podcasts = response.data
          when (response) {
            is Resource.Loading -> {
              if (!podcasts.isNullOrEmpty()) {
                _podcastsSearchResult.update { podcasts }
                stopLoading()
              } else {
                startLoading()
              }
            }

            is Resource.Error -> {
              stopLoading()
              toast(ToastMessage.SERVICE_ERROR)
            }

            is Resource.Success -> {
              if (podcasts.isNullOrEmpty()) {
                toast(ToastMessage.EMPTY_RESPONSE)
              } else {
                _podcastsSearchResult.update { podcasts }
              }
              stopLoading()
            }
          }
        }
    }
  }

  fun onSearchActivated(active: Boolean) {
    _mode.update { if (active) UiMode.SEARCH else UiMode.SUBSCRIBED }
  }

  private fun startLoading() = _isLoading.update { true }

  private fun stopLoading() = _isLoading.update { false }

  private fun toast(message: ToastMessage) = toastHandler?.invoke(message)
}

enum class UiMode {
  SEARCH, SUBSCRIBED
}

sealed interface PodcastsUiState {
  data object Loading : PodcastsUiState
  data object Error : PodcastsUiState
  data class Success(
    val isLoading: Boolean,
    val podcasts: List<Podcast>,
    val mode: UiMode,
  ) : PodcastsUiState
}
