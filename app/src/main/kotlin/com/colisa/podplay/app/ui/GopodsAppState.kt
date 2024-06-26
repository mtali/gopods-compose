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
package com.colisa.podplay.app.ui

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.colisa.podplay.core.utils.isRunning
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Composable
fun rememberGopodsAppState(
  navController: NavHostController = rememberNavController(),
  coroutineScope: CoroutineScope = rememberCoroutineScope(),
): GopodsAppState {
  return GopodsAppState(
    navController = navController,
    coroutineScope = coroutineScope,
  )
}

@Stable
@SuppressLint("RestrictedApi")
class GopodsAppState(
  val navController: NavHostController,
  val coroutineScope: CoroutineScope,
) {

  private var backJob: Job? = null

  val backStack = navController.currentBackStack
    .map { stackEntries ->
      stackEntries.map { entry -> entry.destination.route }
    }
    .stateIn(
      scope = coroutineScope,
      started = SharingStarted.WhileSubscribed(5_000),
      initialValue = emptyList(),
    )

  /**
   * TODO: Replace this with actual navigation
   */
  fun onBackClick() {
    if (backJob.isRunning()) return
    backJob = coroutineScope.launch {
      navController.popBackStack()
      delay(500)
    }
  }
}
