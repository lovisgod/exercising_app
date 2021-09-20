/*
 * Copyright (c) 2021 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * This project and source code may use libraries or frameworks that are
 * released under various Open-Source licenses. Use of those libraries and
 * frameworks are governed by their own individual licenses.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.raywenderlich.android.petbuddy.transitions

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionEvent
import com.google.android.gms.location.ActivityTransitionResult
import com.raywenderlich.android.petbuddy.BuildConfig
import com.raywenderlich.android.petbuddy.SupportedActivity

const val TRANSITIONS_RECEIVER_ACTION = "${BuildConfig.APPLICATION_ID}_transitions_receiver_action"
private const val TRANSITION_PENDING_INTENT_REQUEST_CODE = 200

class TransitionsReceiver: BroadcastReceiver() {

  var action: ((SupportedActivity) -> Unit)? = null

  companion object {

    fun getPendingIntent(context: Context): PendingIntent {
      val intent = Intent(TRANSITIONS_RECEIVER_ACTION)
      return PendingIntent.getBroadcast(context, TRANSITION_PENDING_INTENT_REQUEST_CODE, intent,
          PendingIntent.FLAG_UPDATE_CURRENT)
    }
  }

  override fun onReceive(context: Context, intent: Intent) {
    Toast.makeText(context, "Transitions intent $intent", Toast.LENGTH_SHORT).show()
    // Extract result from the intent and handle transition events
    // 1
    if (ActivityTransitionResult.hasResult(intent)) {
      // 2
      val result = ActivityTransitionResult.extractResult(intent)
      result?.let { handleTransitionEvents(it.transitionEvents) }
    }

  }

  // Filter Enter transitions, map them to SupportedAction, and invoke action with them
  private fun handleTransitionEvents(transitionEvents: List<ActivityTransitionEvent>) {
    transitionEvents
      // 3
      .filter { it.transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER }
      // 4
      .forEach { action?.invoke(SupportedActivity.fromActivityType(it.activityType)) }
  }

}
