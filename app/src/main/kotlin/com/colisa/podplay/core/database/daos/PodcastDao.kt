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

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.colisa.podplay.core.database.entities.PodcastEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PodcastDao {

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  fun insertPodcast(entity: PodcastEntity): Long

  @Update
  fun updatePodcast(entity: PodcastEntity)

  @Query("SELECT subscribed FROM podcasts WHERE id = :id")
  fun getPodcastSubscription(id: Long): Boolean?

  /**
   * Specialized upsert, here we just avoid overriding subscription state
   */
  @Transaction
  fun upsertPodcast(entity: PodcastEntity) {
    val result = insertPodcast(entity)
    if (result == -1L) {
      val subscribed = getPodcastSubscription(entity.id) ?: return
      updatePodcast(entity.copy(subscribed = subscribed))
    }
  }

  @Query("SELECT * FROM podcasts WHERE feed_url = :feedUrl")
  fun getPodcast(feedUrl: String): Flow<PodcastEntity>

  @Query("SELECT * FROM podcasts WHERE subscribed = :subscribed")
  fun getPodcasts(subscribed: Boolean): Flow<List<PodcastEntity>>

  @Query("SELECT * FROM podcasts WHERE collection_id in (:collectionIds)")
  fun getPodcasts(collectionIds: List<Long>): Flow<List<PodcastEntity>>

  @Query("UPDATE podcasts SET subscribed = NOT subscribed WHERE id = :id")
  suspend fun toggleSubscription(id: Long)
}
