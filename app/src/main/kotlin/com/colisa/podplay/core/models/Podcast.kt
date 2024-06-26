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
package com.colisa.podplay.core.models

import java.time.LocalDateTime

data class Podcast(
  val id: Long,
  val collectionId: Long,
  val feedUrl: String,
  val feedTitle: String,
  val feedDescription: String,
  val imageUrl: String,
  val imageUrl600: String,
  val releaseDate: LocalDateTime,
  val subscribed: Boolean = false,
  val episodes: List<Episode> = emptyList(),
)
