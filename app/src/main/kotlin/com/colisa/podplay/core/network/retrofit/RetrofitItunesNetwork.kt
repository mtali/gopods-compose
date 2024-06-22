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
package com.colisa.podplay.core.network.retrofit

import com.colisa.podplay.core.network.ItunesDataSource
import com.colisa.podplay.core.network.models.ItunesPodcastSearchResponse
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

const val ITUNES_BASE_URL = "https://itunes.apple.com"

private interface RetrofitItunesNetworkApi {
  @GET("/search?media=podcast")
  suspend fun searchPodcasts(@Query("term") term: String): ItunesPodcastSearchResponse
}

@Singleton
class RetrofitItunesNetwork @Inject constructor(
  networkJson: Json,
  okhttpCallFactory: dagger.Lazy<Call.Factory>,
) : ItunesDataSource {

  private val networkApi =
    Retrofit.Builder()
      .baseUrl(ITUNES_BASE_URL)
      // We use callFactory lambda here with dagger.Lazy<Call.Factory>
      // to prevent initializing OkHttp on the main thread.
      .callFactory { okhttpCallFactory.get().newCall(it) }
      .addConverterFactory(
        networkJson.asConverterFactory("application/json".toMediaType()),
      )
      .build()
      .create(RetrofitItunesNetworkApi::class.java)

  override suspend fun searchPodcasts(term: String): ItunesPodcastSearchResponse {
    return networkApi.searchPodcasts(term)
  }
}
