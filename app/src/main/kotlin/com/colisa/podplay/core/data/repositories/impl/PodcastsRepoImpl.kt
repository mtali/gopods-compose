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
package com.colisa.podplay.core.data.repositories.impl

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.colisa.podplay.core.data.mediators.EpisodesFeedMediator
import com.colisa.podplay.core.data.repositories.PodcastsRepo
import com.colisa.podplay.core.database.GoDatabase
import com.colisa.podplay.core.database.daos.EpisodeDao
import com.colisa.podplay.core.database.daos.PodcastDao
import com.colisa.podplay.core.database.daos.PodcastSearchResultDao
import com.colisa.podplay.core.database.entities.PodcastSearchResultEntity
import com.colisa.podplay.core.database.entities.toDomain
import com.colisa.podplay.core.models.Episode
import com.colisa.podplay.core.models.Podcast
import com.colisa.podplay.core.network.ItunesDataSource
import com.colisa.podplay.core.network.RssFeedDataSource
import com.colisa.podplay.core.network.dispatchers.Dispatcher
import com.colisa.podplay.core.network.dispatchers.GoDispatcher.IO
import com.colisa.podplay.core.network.models.asPodcastEntities
import com.colisa.podplay.core.network.utils.Resource
import com.colisa.podplay.core.network.utils.networkBoundResource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PodcastsRepoImpl @Inject constructor(
  private val db: GoDatabase,
  private val itunesDataSource: ItunesDataSource,
  private val podcastSearchResultDao: PodcastSearchResultDao,
  private val podcastDao: PodcastDao,
  private val episodeDao: EpisodeDao,
  private val rssFeedDataSource: RssFeedDataSource,
  @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
) : PodcastsRepo {

  override fun searchPodcasts(term: String): Flow<Resource<List<Podcast>>> =
    networkBoundResource(
      db = {
        val search = podcastSearchResultDao.getSearchResult(term).first()
        podcastDao.getPodcasts(collectionIds = search?.collectionIds ?: emptyList()).map { it.toDomain() }
      },
      fetch = {
        itunesDataSource.searchPodcasts(term)
      },
      saveFetchResult = { response ->
        val collectionIds = response.results.map { it.collectionId }
        val searchResultEntity = PodcastSearchResultEntity(term, collectionIds = collectionIds, count = response.resultCount)
        val podcasts = response.asPodcastEntities()
        db.runInTransaction {
          podcasts.forEach { podcastDao.upsertPodcast(it) }
          podcastSearchResultDao.insertSearchResult(searchResultEntity)
        }
      },
      shouldFetch = { true },
    ).flowOn(ioDispatcher)

  override fun getPodcasts(subscribed: Boolean): Flow<List<Podcast>> {
    return podcastDao.getPodcasts(true).map { it.toDomain() }
  }

  override fun requirePodcast(feedUrl: String): Flow<Podcast> =
    podcastDao.getPodcast(feedUrl).map { it.toDomain() }.flowOn(ioDispatcher)

  @OptIn(ExperimentalPagingApi::class)
  override fun getEpisodesPaged(podcastId: Long, feedUrl: String): Flow<PagingData<Episode>> {
    return Pager(
      config = PagingConfig(pageSize = 20, enablePlaceholders = false),
      pagingSourceFactory = {
        episodeDao.getEpisodesPaged(podcastId)
      },
      remoteMediator = EpisodesFeedMediator(db = db, feedDataSource = rssFeedDataSource, feedUrl = feedUrl),
    ).flow.map { pageData -> pageData.map { episode -> episode.toDomain() } }
  }

  override suspend fun toggleSubscription(podcastId: Long) = withContext(ioDispatcher) {
    podcastDao.toggleSubscription(podcastId)
  }
}
