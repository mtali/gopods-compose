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
package com.colisa.podplay.feaure.podcast.navigation

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.colisa.podplay.feaure.podcast.PodcastDetailRoute

private const val FEED_URL = "feedUrl"
private const val PODCAST_ID = "podcastId"
const val PODCAST_DETAIL_ROUTE = "podcast_detail_route/{$PODCAST_ID}/{$FEED_URL}"

class PodcastDetailArg(val podcastId: Long, val feedUrl: String) {
  constructor(savedStateHandle: SavedStateHandle) :
    this(
      podcastId = checkNotNull(savedStateHandle.get<Long>(PODCAST_ID)),
      feedUrl = Uri.decode(checkNotNull(savedStateHandle.get<String>(FEED_URL))),
    )
}

fun NavController.navigateToPostDetail(podcastId: Long, feedUrl: String, navOptions: NavOptions? = null) {
  navigate("podcast_detail_route/$podcastId/${Uri.encode(feedUrl)}", navOptions)
}

fun NavGraphBuilder.podcastDetailScreen(onBackClick: () -> Unit) {
  composable(
    route = PODCAST_DETAIL_ROUTE,
    arguments = listOf(
      navArgument(FEED_URL) { type = NavType.StringType },
      navArgument(PODCAST_ID) { type = NavType.LongType },
    ),
  ) {
    PodcastDetailRoute(onBackClick = onBackClick)
  }
}
