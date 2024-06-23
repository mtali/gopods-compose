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
package com.colisa.podplay.core.network.models

data class RssPodcastResponse(
  val url: String,
  val title: String,
  val description: String,
  val lastBuildDate: String,
  val episodes: List<RssFeedEpisode>,
)

data class RssFeedEpisode(
  val author: String?,
  val title: String?,
  val content: String?,
  val audio: String?,
  val description: String?,
  val guid: String?,
  val pubDate: String?,
  val image: String?,
  val video: String?,
  val link: String?,
)
