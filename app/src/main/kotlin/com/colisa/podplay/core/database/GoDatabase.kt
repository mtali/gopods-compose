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
package com.colisa.podplay.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.colisa.podplay.core.database.daos.EpisodeDao
import com.colisa.podplay.core.database.daos.PodcastDao
import com.colisa.podplay.core.database.daos.PodcastSearchResultDao
import com.colisa.podplay.core.database.entities.EpisodeEntity
import com.colisa.podplay.core.database.entities.PodcastEntity
import com.colisa.podplay.core.database.entities.PodcastSearchResultEntity
import com.colisa.podplay.core.database.utils.CsvListConverter
import com.colisa.podplay.core.database.utils.InstantConverters

@Database(
  version = 1,
  exportSchema = false,
  entities = [
    PodcastEntity::class,
    EpisodeEntity::class,
    PodcastSearchResultEntity::class,
  ],
)
@TypeConverters(InstantConverters::class, CsvListConverter::class)
abstract class GoDatabase : RoomDatabase() {

  abstract fun episodeDao(): EpisodeDao
  abstract fun podcastDao(): PodcastDao
  abstract fun podcastSearchResultDao(): PodcastSearchResultDao

  companion object {
    @Volatile
    private var instance: GoDatabase? = null

    fun getInstance(context: Context): GoDatabase {
      return instance ?: synchronized(this) {
        instance ?: buildDatabase(context).also { instance = it }
      }
    }

    private fun buildDatabase(context: Context): GoDatabase {
      return Room.databaseBuilder(context, GoDatabase::class.java, "GoDatabase")
        .build()
    }
  }
}
