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
package com.colisa.podplay.app.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.colisa.podplay.app.ui.GopodsAppState
import com.colisa.podplay.feaure.podcast.navigation.navigateToPostDetail
import com.colisa.podplay.feaure.podcast.navigation.podcastDetailScreen
import com.colisa.podplay.feaure.podcasts.navigation.podcastsScreen
import com.colisa.podplay.feaure.settings.navigation.navigateToSettings
import com.colisa.podplay.feaure.settings.navigation.settingsScreen

@Composable
fun GopodsNavHost(
  startDestination: String,
  appState: GopodsAppState,
  modifier: Modifier = Modifier,
) {
  val navController = appState.navController

  NavHost(
    navController = navController,
    startDestination = startDestination,
    modifier = modifier,
    enterTransition = {
      slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(150))
    },
    exitTransition = {
      slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(150))
    },
    popEnterTransition = {
      slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(150))
    },
    popExitTransition = {
      slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(150))
    },

  ) {
    settingsScreen()
    podcastDetailScreen(onBackClick = { appState.onBackClick() })
    podcastsScreen(
      onClickAbout = {},
      onClickSettings = {
        navController.navigateToSettings()
      },
      onSelectPodcast = { feedUrl ->
        navController.navigateToPostDetail(feedUrl)
      },
    )
  }
}
