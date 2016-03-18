package info.tatsu.android.wakelock_tile;

import android.content.ComponentName;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import static android.os.PowerManager.SCREEN_DIM_WAKE_LOCK;

public class WakelockQSTileService extends TileService
{
    private static final String TAG = WakelockQSTileService.class.getName();
    private static final String IS_ADDED_PREFERENCE = "isAdded";

    private SharedPreferences m_preferences;
    private PowerManager m_powerManager;
    private WakeLock m_wakelock;
    private Tile m_tile;

    @Override
    public void onCreate()
    {
        super.onCreate();
        m_preferences = getSharedPreferences("QS", MODE_PRIVATE);
        m_powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        if (m_preferences.getBoolean(IS_ADDED_PREFERENCE, false))
        {
            requestListeningState(this, new ComponentName(this, getClass()));
        }
    }

    @Override
    public void onDestroy() {
        m_powerManager = null;
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
        if (m_powerManager.isWakeLockLevelSupported(SCREEN_DIM_WAKE_LOCK))
        {
            m_wakelock = m_powerManager.newWakeLock(SCREEN_DIM_WAKE_LOCK, TAG);
        }
        m_tile = getQsTile();
        updateTile();
    }

    @Override
    public void onStopListening()
    {
        m_tile = null;
        if (m_wakelock != null && m_wakelock.isHeld())
        {
            m_wakelock.release();
        }
        m_wakelock = null;
        m_tile = null;
    }

    @Override
    public void onClick()
    {
        if (m_wakelock != null)
        {
            boolean isHeld = m_wakelock.isHeld();
            if (isHeld)
            {
                m_wakelock.release();
            }
            else
            {
                m_wakelock.acquire();
            }
        }
        updateTile();
    }

    private void updateTile() {
        m_tile.setState(
                m_wakelock == null ? Tile.STATE_UNAVAILABLE :
                m_wakelock.isHeld() ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        m_tile.updateTile();
    }
}
