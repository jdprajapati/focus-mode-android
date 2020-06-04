package com.maulik.focusmode.extensions

import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Context.AUDIO_SERVICE
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.annotation.AnimRes

inline fun <reified T : Activity> Context.startActivity(block: Intent.() -> Unit = {}) {
    val intent = Intent(this, T::class.java)
    block(intent)
    startActivity(intent)
}

inline fun <reified T : Activity> Activity.startActivityAndFinish(block: Intent.() -> Unit = {}) {
    val intent = Intent(this, T::class.java)
    block(intent)
    startActivity(intent)
    finish()
}

private class AnimationListener(
    private val onAnimationRepeat: () -> Unit,
    private val onAnimationStart: () -> Unit,
    private val onAnimationEnd: () -> Unit
) : Animation.AnimationListener {
    override fun onAnimationRepeat(p0: Animation?) = onAnimationRepeat()
    override fun onAnimationStart(p0: Animation?) = onAnimationStart()
    override fun onAnimationEnd(p0: Animation?) = onAnimationEnd()
}

fun View.playAnimation(
    @AnimRes animResId: Int,
    onAnimationRepeat: () -> Unit = {},
    onAnimationStart: () -> Unit = {},
    onAnimationEnd: () -> Unit = {}
) = with(AnimationUtils.loadAnimation(context, animResId)) {
    setAnimationListener(AnimationListener(onAnimationRepeat, onAnimationStart, onAnimationEnd))
    startAnimation(this)
}

fun Context.isNotificationPermissionAllowed(): Boolean {
    val manager: NotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        manager.isNotificationPolicyAccessGranted
    } else {
        true
    }
}

fun Context.showToast(message: String, showLong: Boolean = false) {
    Toast.makeText(
        this,
        message,
        if (showLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
    ).show()
}

fun Context.toggleDnd(enable: Boolean) {
    val manager: NotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && isNotificationPermissionAllowed()) {
        manager.setInterruptionFilter(if (enable) NotificationManager.INTERRUPTION_FILTER_NONE else NotificationManager.INTERRUPTION_FILTER_ALL)
    }
}

fun Context.toggleSilentMode(enable: Boolean) {
    val manager: AudioManager = getSystemService(AUDIO_SERVICE) as AudioManager
    if (isNotificationPermissionAllowed()) {
        manager.ringerMode =
            if (enable) AudioManager.RINGER_MODE_SILENT else AudioManager.RINGER_MODE_NORMAL
    }
}
