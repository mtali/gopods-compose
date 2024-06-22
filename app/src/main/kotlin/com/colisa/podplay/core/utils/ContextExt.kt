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
package com.colisa.podplay.core.utils

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import com.colisa.podplay.R
import com.colisa.podplay.core.models.ToastMessage

fun Context.toast(@StringRes msg: Int) {
  Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Context.toast(message: ToastMessage) {
  val res = when (message) {
    ToastMessage.SERVICE_ERROR -> R.string.service_error
    ToastMessage.EMPTY_RESPONSE -> R.string.empty_response
  }
  toast(res)
}
