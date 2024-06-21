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
package com.colisa.podplay.core.database.utils

import androidx.room.TypeConverter
import timber.log.Timber
import java.time.Instant

object InstantConverters {
  @TypeConverter
  fun longToInstant(value: Long?): Instant? = value?.let(Instant::ofEpochMilli)

  @TypeConverter
  fun instantToLong(instant: Instant?): Long? = instant?.toEpochMilli()
}

object CsvListConverter {
  @TypeConverter
  fun csvToList(data: String?): List<Long>? {
    return data?.let {
      it.split(",").map { str ->
        try {
          str.toLong()
        } catch (e: NumberFormatException) {
          Timber.e(e, "Cannot convert $str to number")
          null
        }
      }
    }?.filterNotNull()
  }

  @TypeConverter
  fun listToCsv(list: List<Long>?): String? {
    return list?.joinToString(",")
  }
}
