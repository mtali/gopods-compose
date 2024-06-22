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
package com.colisa.podplay.core.network.models

import com.colisa.podplay.core.database.entities.PodcastEntity
import kotlinx.serialization.Serializable

@Serializable
data class NetworkItunesPodcast(
  val collectionId: Long,
  val collectionName: String,
  val feedUrl: String? = null,
  val artworkUrl100: String,
  val artworkUrl600: String,
  val releaseDate: String,
)

@Serializable
data class ItunesPodcastSearchResponse(
  val resultCount: Int,
  val results: List<NetworkItunesPodcast>,
)

fun ItunesPodcastSearchResponse.asPodcastEntities() =
  if (results.isEmpty()) {
    emptyList()
  } else {
    results
      .filter { it.feedUrl != null }
      .map {
        PodcastEntity(
          id = null,
          collectionId = it.collectionId,
          feedUrl = it.feedUrl!!,
          feedTitle = it.collectionName,
          feedDescription = "",
          imageUrl = it.artworkUrl100,
          imageUrl600 = it.artworkUrl600,
          releaseDate = it.releaseDate,
        )
      }
  }
