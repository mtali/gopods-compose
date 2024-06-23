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

import com.colisa.podplay.core.data.repositories.PodcastsRepo
import com.colisa.podplay.core.database.GoDatabase
import com.colisa.podplay.core.database.daos.EpisodeDao
import com.colisa.podplay.core.database.daos.PodcastDao
import com.colisa.podplay.core.database.daos.PodcastSearchResultDao
import com.colisa.podplay.core.database.entities.PodcastSearchResultEntity
import com.colisa.podplay.core.database.entities.toDomain
import com.colisa.podplay.core.models.Podcast
import com.colisa.podplay.core.network.ItunesDataSource
import com.colisa.podplay.core.network.RssFeedDataSource
import com.colisa.podplay.core.network.dispatchers.Dispatcher
import com.colisa.podplay.core.network.dispatchers.GoDispatcher.IO
import com.colisa.podplay.core.network.models.asEpisodeEntities
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

  override fun getPodcast(podcastId: Long): Flow<Podcast?> =
    podcastDao.getPodcast(id = podcastId).map { entity -> entity?.toDomain() }

  override fun getPodcastFeed(feedUrl: String): Flow<Resource<Podcast>> =
    networkBoundResource(
      db = {
        podcastDao.getPodcastWithEpisodes(feedUrl).map { it.toDomain() }
      },
      fetch = {
        rssFeedDataSource.fetchPodcastRssFeed(feedUrl)
      },
      saveFetchResult = { response ->
        val dbPodcast = podcastDao.getPodcast(feedUrl).first()!!
        val episodes = response.asEpisodeEntities(dbPodcast.id)
        db.runInTransaction {
          podcastDao.upsertPodcast(dbPodcast.copy(feedDescription = response.description))
          episodeDao.insertEpisodes(episodes)
        }
      },
      shouldFetch = { true },
    ).flowOn(ioDispatcher)

  override suspend fun toggleSubscription(podcastId: Long) = withContext(ioDispatcher) {
    podcastDao.toggleSubscription(podcastId)
  }
}
