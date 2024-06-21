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
package com.colisa.podplay.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.colisa.podplay.core.utils.now
import java.time.Instant

@Entity(
  indices = [Index(value = ["collection_id"], unique = true)],
  tableName = "podcasts",
)
data class PodcastEntity(
  @PrimaryKey val id: Long? = null,
  @ColumnInfo(name = "collection_id") val collectionId: Long = 0,
  @ColumnInfo(name = "feed_url") val feedUrl: String = "",
  @ColumnInfo(name = "feed_title") val feedTitle: String = "",
  @ColumnInfo(name = "feed_description") val feedDescription: String = "",
  @ColumnInfo(name = "image_url") val imageUrl: String = "",
  @ColumnInfo(name = "image_url600") val imageUrl600: String = "",
  @ColumnInfo(name = "last_updated") val lastUpdated: Instant = now(),
  @ColumnInfo(name = "subscribed") val subscribed: Boolean = false,
)
