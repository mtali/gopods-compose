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
package com.colisa.podplay.core.network.rss

import com.colisa.podplay.core.network.RssFeedDataSource
import com.colisa.podplay.core.network.dispatchers.Dispatcher
import com.colisa.podplay.core.network.dispatchers.GoDispatcher.Default
import com.colisa.podplay.core.network.models.RssFeedEpisode
import com.colisa.podplay.core.network.models.RssPodcastResponse
import com.prof18.rssparser.RssParser
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class RssFeedNetwork @Inject constructor(
  private val rssParser: RssParser,
  @Dispatcher(Default) private val defaultDispatcher: CoroutineDispatcher,
) : RssFeedDataSource {
  override suspend fun fetchPodcastRssFeed(feedUrl: String): RssPodcastResponse {
    try {
      val channel = rssParser.getRssChannel(feedUrl)
      return withContext(defaultDispatcher) {
        RssPodcastResponse(
          url = feedUrl,
          title = channel.title ?: "",
          description = channel.description ?: "",
          lastBuildDate = channel.lastBuildDate ?: "",
          episodes = channel.items.map {
            RssFeedEpisode(
              author = it.author,
              title = it.title,
              content = it.content,
              audio = it.audio,
              description = it.description,
              guid = it.guid,
              pubDate = it.pubDate,
              image = it.image,
              video = it.video,
              link = it.link,
            )
          },
        )
      }
    } catch (e: Exception) {
      Timber.e(e)
      throw e
    }
  }
}
