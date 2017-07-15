package info.tatsu.android.wakelock_tile

import android.annotation.SuppressLint
import android.app.Notification
import android.app.Service
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.IBinder
import android.os.PowerManager

class WakelockHolderService : Service() {
    override fun onBind(intent: Intent): IBinder? = null

    @SuppressLint("Wakelock")
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int =
            if (intent.getBooleanExtra(WAKELOCK_EXTRA, false)) {
                (application as? App)?.holdAwake()
                startForeground(R.id.notification, Notification.Builder(this).apply {
                    setPriority(Notification.PRIORITY_LOW)
                    setCategory(Notification.CATEGORY_SERVICE)
                    setContentTitle(getString(R.string.qstile_active))
                    setContentText(getString(R.string.qstile_description))
                    setSmallIcon(Icon.createWithResource(this@WakelockHolderService, R.drawable.wakelock))
                    setShowWhen(false)
                    setOngoing(true)
                }.build())
                Service.START_STICKY
            } else {
                (application as? App)?.releaseAwake()
                stopForeground(true)
                Service.START_NOT_STICKY
            }

    companion object {
        const val WAKELOCK_EXTRA = "l"
    }
}
