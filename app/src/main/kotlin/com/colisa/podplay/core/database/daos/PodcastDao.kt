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
package com.colisa.podplay.core.database.daos

import android.util.SparseLongArray
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.colisa.podplay.core.database.entities.PodcastEntity
import com.colisa.podplay.core.database.entities.PodcastWithEpisodesEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Dao
interface PodcastDao {

  @Upsert
  fun upsertPodcast(podcastEntity: PodcastEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertPodcasts(entities: List<PodcastEntity>)

  @Query("DELETE FROM podcasts WHERE id = :id")
  suspend fun deletePodcast(id: Long): Int

  @Query("SELECT * FROM podcasts WHERE feed_url = :feedUrl")
  fun getPodcast(feedUrl: String): Flow<PodcastEntity?>

  @Query("SELECT * FROM podcasts WHERE feed_url = :feedUrl")
  fun getPodcastWithEpisodes(feedUrl: String): Flow<PodcastWithEpisodesEntity>

  @Query("SELECT * FROM podcasts WHERE id = :id")
  fun getPodcast(id: Long): Flow<PodcastEntity?>

  @Query("SELECT * FROM podcasts WHERE subscribed = :subscribed")
  fun getPodcasts(subscribed: Boolean): Flow<List<PodcastEntity>>

  @Query("SELECT * FROM podcasts WHERE collection_id in (:collectionIds)")
  fun getPodcasts(collectionIds: List<Long>): Flow<List<PodcastEntity>>

  fun getOrderedPodcasts(collectionIds: List<Long>): Flow<List<PodcastEntity>> {
    val order = SparseLongArray()
    collectionIds.withIndex().forEach { order.put(it.value.toInt(), it.index.toLong()) }
    return getPodcasts(collectionIds).map { podcasts ->
      podcasts.sortedWith(compareBy { order.get(it.collectionId.toInt()) })
    }
  }
}
