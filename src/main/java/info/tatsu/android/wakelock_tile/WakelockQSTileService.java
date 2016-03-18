package info.tatsu.android.wakelock_tile;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.ComponentName;
import android.content.SharedPreferences;
import android.graphics.drawable.Icon;
import android.os.PowerManager;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

public class WakelockQSTileService extends TileService
{
    private static final String IS_ADDED_PREFERENCE = "isAdded";

    private SharedPreferences m_preferences;
    private Tile m_tile;

    @Override
    public void onCreate()
    {
        super.onCreate();
        m_preferences = getSharedPreferences("QS", MODE_PRIVATE);
        if (m_preferences.getBoolean(IS_ADDED_PREFERENCE, false))
        {
            requestListeningState(this, new ComponentName(this, getClass()));
        }
    }

    @Override
    public void onDestroy() {
        m_preferences = null;
        super.onDestroy();
    }

    @Override
    public int onTileAdded()
    {
        m_preferences.edit().putBoolean(IS_ADDED_PREFERENCE, true).apply();
        requestListeningState(this, new ComponentName(this, getClass()));
        return TILE_MODE_ACTIVE;
    }

    @Override
    public void onTileRemoved()
    {
        m_preferences.edit().putBoolean(IS_ADDED_PREFERENCE, false).apply();
    }

    @Override
    public void onStartListening()
    {
        m_tile = getQsTile();
        PowerManager.WakeLock wakelock = ((App) getApplication()).getWakeLock();
        updateTile(wakelock);
    }

    @Override
    public void onStopListening()
    {
        m_tile = null;
    }

    @Override
    @SuppressLint("Wakelock")
    public void onClick()
    {
        stopForeground(true);
        PowerManager.WakeLock wakelock = ((App) getApplication()).getWakeLock();
        if (wakelock != null)
        {
            boolean isHeld = wakelock.isHeld();
            if (isHeld)
            {
                wakelock.release();
            }
            else
            {
                startForeground(R.id.notification, new Notification.Builder(this)
                        .setPriority(Notification.PRIORITY_LOW)
                        .setCategory(Notification.CATEGORY_SERVICE)
                        .setContentTitle(getString(R.string.qstile_active))
                        .setContentText(getString(R.string.qstile_description))
                        .setSmallIcon(Icon.createWithResource(this, R.drawable.wakelock))
                        .setShowWhen(false)
                        .setOngoing(true)
                        .build());
                wakelock.acquire();
            }
        }
        updateTile(wakelock);
    }

    private void updateTile(PowerManager.WakeLock wakelock) {
        m_tile.setState(
                wakelock == null ? Tile.STATE_UNAVAILABLE :
                wakelock.isHeld() ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        m_tile.updateTile();
    }
}
