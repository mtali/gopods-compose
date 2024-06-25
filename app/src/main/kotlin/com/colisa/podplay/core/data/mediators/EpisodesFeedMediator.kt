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
package com.colisa.podplay.core.data.mediators

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.colisa.podplay.core.database.GoDatabase
import com.colisa.podplay.core.database.entities.EpisodeEntity
import com.colisa.podplay.core.network.RssFeedDataSource
import com.colisa.podplay.core.network.models.asEpisodeEntities
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalPagingApi::class)
class EpisodesFeedMediator(
  private val db: GoDatabase,
  private val feedDataSource: RssFeedDataSource,
  private val feedUrl: String,
) : RemoteMediator<Int, EpisodeEntity>() {

  private val podcastDao = db.podcastDao()
  private val episodeDao = db.episodeDao()

  override suspend fun load(loadType: LoadType, state: PagingState<Int, EpisodeEntity>): MediatorResult {
    return try {
      when (loadType) {
        LoadType.REFRESH -> Unit
        LoadType.PREPEND,
        LoadType.APPEND,
        -> {
          val lastItem = state.lastItemOrNull()
          if (lastItem == null) {
            Unit
          } else {
            return MediatorResult.Success(endOfPaginationReached = true)
          }
        }
      }

      val feed = feedDataSource.fetchPodcastRssFeed(feedUrl)

      val dbPodcast = podcastDao.getPodcast(feedUrl).first()
      db.withTransaction {
        podcastDao.upsertPodcast(dbPodcast.copy(feedDescription = feed.description))
        episodeDao.insertEpisodes(feed.asEpisodeEntities(dbPodcast.id))
      }
      MediatorResult.Success(endOfPaginationReached = true)
    } catch (e: Exception) {
      MediatorResult.Error(e)
    }
  }
}
