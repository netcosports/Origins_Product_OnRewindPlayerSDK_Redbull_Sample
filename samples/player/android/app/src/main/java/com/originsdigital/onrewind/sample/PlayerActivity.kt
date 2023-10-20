package com.originsdigital.onrewind.sample

import android.app.PictureInPictureParams
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Rational
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.origins.onrewind.OnRewind
import com.origins.onrewind.analytics.OnRewindEventListener
import com.origins.onrewind.domain.analytics.AnalyticsEvent
import com.origins.onrewind.domain.models.player.MediaControllerMode
import com.origins.onrewind.domain.models.player.ScreenMode
import com.origins.onrewind.ui.OnRewindPlayerView
import com.origins.onrewind.ui.PlayerParameters
import com.origins.onrewind.ui.util.isPortrait

class PlayerActivity : AppCompatActivity() {

    private val constraintSetFullscreen = ConstraintSet()
    private val constraintSetNormal = ConstraintSet()

    private var isPlayerInPortrait: Boolean = true

    private val isLandOnly: Boolean = false

    private val returnToUserOrientationAction = Runnable {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER
    }

    private var playerActivityContainer: ConstraintLayout? = null

    private var controller: WindowInsetsControllerCompat? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        controller = WindowInsetsControllerCompat(window, window.decorView)
        val isPortrait = resources.configuration.isPortrait()

        setContentView(R.layout.activity_player_normal)
        playerActivityContainer = findViewById(R.id.playerActivityContainer)

        constraintSetFullscreen.clone(this, R.layout.activity_player_fullscreen)
        constraintSetNormal.clone(this, R.layout.activity_player_normal)

        showPlayer()

        playerView?.fullscreenButtonToggleHandler =
            OnRewindPlayerView.FullscreenButtonToggleHandler {
                updateOrientation(!isPlayerInPortrait)
            }

        playerView?.exitEnrichModeHandler = OnRewindPlayerView.ExitEnrichModeHandler {
            updateControllerMode(false)
        }

        playerView?.exitNormalLandscapeHandler = OnRewindPlayerView.ExitNormalLandscapeHandler {
            updateOrientation(true)
            playerView?.requestControllerMode(MediaControllerMode.NORMAL)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            playerView?.onPlayerCanGoPictureInPictureListener =
                OnRewindPlayerView.OnPlayerCanGoPictureInPictureListener { canGoPip ->
                    val topLeftLocation = IntArray(2)
                    val playerContainer = findViewById<View>(R.id.playerContainer)
                    playerContainer.getLocationInWindow(topLeftLocation)

                    val rect = Rect().apply {
                        left = topLeftLocation[0]
                        top = topLeftLocation[1]
                        right = left + playerContainer.width
                        bottom = top + playerContainer.height
                    }

                    val params = PictureInPictureParams.Builder()
                        .setAspectRatio(Rational(16, 9))
                        .setSourceRectHint(rect)
                        .setAutoEnterEnabled(canGoPip)
                        .build()

                    setPictureInPictureParams(params)
                }
        }

        if (!isLandOnly) {
            playerView?.requestControllerMode(MediaControllerMode.NORMAL)
            updateControllerMode(false)
        }

        updatePlayerConstraints(isPortrait)

        findViewById<View>(R.id.enterEnrichedMode).setOnClickListener {
            updateControllerMode(true)
        }
    }

    private fun updateOrientation(isPortrait: Boolean) {
        playerActivityContainer?.removeCallbacks(returnToUserOrientationAction)
        if (!isPortrait) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
        } else {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT
            playerActivityContainer?.postDelayed(returnToUserOrientationAction, 2000)
        }
    }

    override fun onStart() {
        super.onStart()
        //listen to analitics events
        OnRewind.addEventListener(eventListener)
    }

    override fun onStop() {
        super.onStop()
        OnRewind.removeEventListener(eventListener)
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            if (playerView?.canEnterPictureInPicture == true) {
                val topLeftLocation = IntArray(2)
                val playerContainer = findViewById<View>(R.id.playerContainer)
                playerContainer.getLocationInWindow(topLeftLocation)

                val rect = Rect().apply {
                    left = topLeftLocation[0]
                    top = topLeftLocation[1]
                    right = left + playerContainer.width
                    bottom = top + playerContainer.height
                }

                enterPictureInPictureMode(
                    PictureInPictureParams
                        .Builder()
                        .setAspectRatio(Rational(16, 9))
                        .setSourceRectHint(rect)
                        .build()
                )
            }
        }
    }

    private var playerView: OnRewindPlayerView? = null

    private fun showPlayer() {
        val config = PlayerParameters.Builder()
            .setEventId("6227c6cd-c8e5-457e-a2ea-aef675d627ce")
            .setAccountKey("HkXiMvWoU")
            .setIsChromecastEnabled(false)
            .build()

        val playerContainer = findViewById<FrameLayout>(R.id.playerContainer)
        val player = OnRewindPlayerView(this, config)
        playerView = player

        playerContainer.addView(
            player,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }

    private fun updateControllerMode(isEnriched: Boolean) {
        if (isLandOnly) {
            playerView?.requestControllerMode(MediaControllerMode.ENRICHED)
            return
        }

        val mode = if (isEnriched) MediaControllerMode.ENRICHED else MediaControllerMode.NORMAL
        updateOrientation(mode == MediaControllerMode.NORMAL)

        playerView?.requestControllerMode(mode)
    }

    private fun updatePlayerConstraints(isPortrait: Boolean) {
        if (isPlayerInPortrait == isPortrait) return
        isPlayerInPortrait = isPortrait
        val set = if (isPortrait) {
            constraintSetNormal
        } else {
            constraintSetFullscreen
        }
        set.applyTo(playerActivityContainer)
        playerView?.requestScreenMode(if (isPortrait) ScreenMode.NORMAL else ScreenMode.FULLSCREEN)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        updatePlayerConstraints(newConfig.isPortrait())
        updateSystemUiFlags(newConfig.isPortrait())
    }

    override fun onResume() {
        super.onResume()
        updateSystemUiFlags(isPlayerInPortrait)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        updateSystemUiFlags(isPlayerInPortrait)
    }

    private fun updateSystemUiFlags(isPortrait: Boolean) {
        if (isPortrait) {
            showSystemBars()
        } else {
            hideSystemBars()
        }
    }

    private fun hideSystemBars() {
        controller?.hide(WindowInsetsCompat.Type.systemBars())
        controller?.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

    private fun showSystemBars() {
        controller?.show(WindowInsetsCompat.Type.systemBars())
        WindowCompat.setDecorFitsSystemWindows(window, true)
    }

    private val eventListener: OnRewindEventListener = object : OnRewindEventListener {
        override fun onEvent(event: AnalyticsEvent) {
            Log.i("PlayerSample", event.toString())
        }
    }
}