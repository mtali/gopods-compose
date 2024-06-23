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
package com.colisa.podplay.core.network.di

import android.content.Context
import com.colisa.podplay.BuildConfig
import com.colisa.podplay.core.network.ItunesDataSource
import com.colisa.podplay.core.network.RssFeedDataSource
import com.colisa.podplay.core.network.dispatchers.Dispatcher
import com.colisa.podplay.core.network.dispatchers.GoDispatcher.Default
import com.colisa.podplay.core.network.dispatchers.GoDispatcher.IO
import com.colisa.podplay.core.network.dispatchers.GoDispatcher.Main
import com.colisa.podplay.core.network.retrofit.RetrofitItunesNetwork
import com.colisa.podplay.core.network.rss.RssFeedNetwork
import com.prof18.rssparser.RssParser
import com.prof18.rssparser.RssParserBuilder
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {

  @Binds
  @Singleton
  abstract fun bindItunesDataResource(impl: RetrofitItunesNetwork): ItunesDataSource

  @Binds
  @Singleton
  abstract fun bindsRssFeedDataSource(impl: RssFeedNetwork): RssFeedDataSource

  companion object {
    @Provides
    @Singleton
    fun provideRssParser(callFactory: Call.Factory): RssParser {
      return RssParserBuilder(
        callFactory = callFactory,
        charset = Charset.forName("ISO-8859-7"),
      ).build()
    }

    @Provides
    @Singleton
    fun providesNetworkJson(): Json = Json {
      ignoreUnknownKeys = true
    }

    @Provides
    @Singleton
    fun okHttpCallFactory(@ApplicationContext context: Context): Call.Factory {
      return OkHttpClient.Builder()
        .cache(
          Cache(
            directory = File(context.cacheDir, "http_cache"),
            maxSize = 50L * 1024L * 1024L, // 50 MiB
          ),
        )
        .addInterceptor { chain ->
          val response = chain.proceed(chain.request())
          val cacheControl = CacheControl.Builder()
            .maxAge(15, TimeUnit.MINUTES)
            .build()
          response.newBuilder()
            .header("Cache-Control", cacheControl.toString())
            .build()
        }
        .addInterceptor(
          HttpLoggingInterceptor()
            .apply {
              if (BuildConfig.DEBUG) {
                setLevel(HttpLoggingInterceptor.Level.BODY)
              }
            },
        )
        .build()
    }

    @Provides
    @Dispatcher(IO)
    fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Dispatcher(Default)
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @Provides
    @Dispatcher(Main)
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main
  }
}
