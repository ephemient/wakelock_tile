package info.tatsu.android.wakelock_tile;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.IBinder;
import android.os.PowerManager;

public class WakelockHolderService extends Service
{
    public static final String WAKELOCK_EXTRA = "l";

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    @SuppressLint("Wakelock")
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        PowerManager.WakeLock wakelock = ((App) getApplication()).getWakeLock();
        if (intent.getBooleanExtra(WAKELOCK_EXTRA, false))
        {
            if (!wakelock.isHeld())
            {
                wakelock.acquire();
            }
            startForeground(R.id.notification, new Notification.Builder(this)
                    .setPriority(Notification.PRIORITY_LOW)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .setContentTitle(getString(R.string.qstile_active))
                    .setContentText(getString(R.string.qstile_description))
                    .setSmallIcon(Icon.createWithResource(this, R.drawable.wakelock))
                    .setShowWhen(false)
                    .setOngoing(true)
                    .build());
        }
        else
        {
            if (wakelock.isHeld())
            {
                wakelock.release();
            }
            stopForeground(true);
        }
        if (wakelock.isHeld())
        {
            return START_STICKY;
        }
        else
        {
            stopSelfResult(startId);
            return START_NOT_STICKY;
        }
    }
}
