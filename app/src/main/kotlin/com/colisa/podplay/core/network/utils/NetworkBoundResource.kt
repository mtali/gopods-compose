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
package com.colisa.podplay.core.network.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

inline fun <ResultType, RequestType> networkBoundResource(
  crossinline db: suspend () -> Flow<ResultType>,
  crossinline fetch: suspend () -> RequestType,
  crossinline saveFetchResult: suspend (RequestType) -> Unit,
  crossinline shouldFetch: (ResultType) -> Boolean = { true },
) = flow {
  val data = db().first()

  val resource = if (shouldFetch(data)) {
    emit(Resource.Loading(data))

    try {
      val resultType = fetch()

      saveFetchResult(resultType)

      db().map { Resource.Success(it) }
    } catch (throwable: Throwable) {
      db().map { Resource.Error(throwable, it) }
    }
  } else {
    db().map { Resource.Success(it) }
  }

  emitAll(resource)
}
