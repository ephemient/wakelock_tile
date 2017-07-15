package info.tatsu.android.wakelock_tile

import android.app.Application
import android.content.Context
import android.os.PowerManager

class App : Application() {
    private var wakeLock: PowerManager.WakeLock? = null

    val heldAwake
        get() = wakeLock?.isHeld

    fun holdAwake() {
        if (!(heldAwake ?: true)) {
            wakeLock?.acquire()
        }
    }

    fun releaseAwake() {
        if (heldAwake ?: false) {
            wakeLock?.release()
        }
    }

    override fun onCreate() {
        super.onCreate()
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = if (powerManager.isWakeLockLevelSupported(PowerManager.SCREEN_DIM_WAKE_LOCK)) {
            powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, TAG)
        } else null
    }

    companion object {
        private val TAG = App::class.java.`package`.name
    }
}
