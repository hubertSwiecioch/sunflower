/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.samples.apps.sunflower

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.samples.apps.sunflower.extensions.makeStatusBarTransparent
import com.google.samples.apps.sunflower.extensions.showSnackBarWithAction
import kotlinx.android.synthetic.main.activity_splash.*


class SplashActivity : AppCompatActivity() {

    private val splashTime = 3000L // 3 seconds
    private lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        makeStatusBarTransparent()

        handler = Handler()
        handler.postDelayed({
            authenticateUser()
        }, splashTime)
    }

    private fun authenticateUser() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(listOf(
                                AuthUI.IdpConfig.GoogleBuilder().build(),
//                                AuthUI.IdpConfig.FacebookBuilder().build(),
                                AuthUI.IdpConfig.TwitterBuilder().build(),
                                AuthUI.IdpConfig.MicrosoftBuilder().build(),
                                AuthUI.IdpConfig.YahooBuilder().build(),
                                AuthUI.IdpConfig.AppleBuilder().build(),
                                AuthUI.IdpConfig.EmailBuilder().build(),
                                AuthUI.IdpConfig.PhoneBuilder().build(),
                                AuthUI.IdpConfig.AnonymousBuilder().build()))
                        .build(),
                RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)
            // Successfully signed in
            if (resultCode == Activity.RESULT_OK) {
                startActivity(GardenActivity.createIntent(this))
                finish()
            } else { // Sign in failed
                if (response == null) { // User pressed back button
                    showSnackBarWithAction(splashContainer, R.string.sign_in_cancelled, R.string.retry) { authenticateUser() }
                    return
                }
                if (response.error!!.errorCode == ErrorCodes.NO_NETWORK) {
                    showSnackBarWithAction(splashContainer, R.string.no_internet_connection, R.string.retry) { authenticateUser() }
                    return
                }
                showSnackBarWithAction(splashContainer, R.string.unknown_error, R.string.retry) { authenticateUser() }
                Log.e(SplashActivity::javaClass.name, "Sign-in error: ", response.error)
            }
        }
    }

    companion object {
        private const val RC_SIGN_IN = 123
    }
}