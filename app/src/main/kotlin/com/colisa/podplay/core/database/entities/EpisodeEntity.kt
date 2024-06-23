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
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.colisa.podplay.core.models.Episode
import com.colisa.podplay.core.utils.newUid

@Entity(
  foreignKeys = [
    ForeignKey(
      entity = PodcastEntity::class,
      parentColumns = ["id"],
      childColumns = ["podcast_id"],
      onDelete = ForeignKey.CASCADE,
    ),
  ],
  indices = [Index("podcast_id")],
  tableName = "episodes",
)
data class EpisodeEntity(
  @PrimaryKey val guid: String = newUid(),
  @ColumnInfo(name = "podcast_id") val podcastId: Long? = null,
  @ColumnInfo(name = "title") val title: String,
  @ColumnInfo(name = "description") val description: String,
  @ColumnInfo(name = "media_url") val mediaUrl: String,
  @ColumnInfo(name = "release_date") val releaseDate: String,
  @ColumnInfo(name = "duration") val duration: String,
)

fun EpisodeEntity.toDomain() =
  Episode(
    guid = guid,
    title = title,
    description = description,
    mediaUrl = mediaUrl,
    releaseDate = releaseDate,
    duration = duration,
  )
