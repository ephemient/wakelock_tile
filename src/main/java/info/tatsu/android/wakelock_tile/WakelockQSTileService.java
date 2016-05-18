package info.tatsu.android.wakelock_tile;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
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
    public void onTileAdded()
    {
        m_preferences.edit().putBoolean(IS_ADDED_PREFERENCE, true).apply();
        requestListeningState(this, new ComponentName(this, getClass()));
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
        updateTile(wakelock == null ? null : wakelock.isHeld());
    }

    @Override
    public void onStopListening()
    {
        m_tile = null;
    }

    @Override
    public void onClick()
    {
        stopForeground(true);
        PowerManager.WakeLock wakelock = ((App) getApplication()).getWakeLock();
        if (wakelock == null)
        {
            updateTile(null);
        }
        else if (wakelock.isHeld())
        {
            Intent intent = new Intent(this, WakelockHolderService.class);
            intent.addCategory(Intent.ACTION_RUN);
            intent.putExtra(WakelockHolderService.WAKELOCK_EXTRA, false);
            startService(intent);
            updateTile(Boolean.FALSE);
        }
        else
        {
            Intent intent = new Intent(this, WakelockHolderService.class);
            intent.addCategory(Intent.ACTION_RUN);
            intent.putExtra(WakelockHolderService.WAKELOCK_EXTRA, true);
            startService(intent);
            updateTile(Boolean.TRUE);
        }
    }

    private void updateTile(Boolean isHeld) {
        m_tile.setState(
                isHeld == null ? Tile.STATE_UNAVAILABLE :
                isHeld ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        m_tile.updateTile();
    }
}
