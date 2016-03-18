package info.tatsu.android.wakelock_tile;

import android.app.Application;
import android.os.PowerManager;

public class App extends Application
{
    private static final String TAG = App.class.getPackage().getName();

    private PowerManager m_powerManager;
    private PowerManager.WakeLock m_wakelock;

    @Override
    public void onCreate()
    {
        super.onCreate();
        m_powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        if (m_powerManager.isWakeLockLevelSupported(PowerManager.SCREEN_DIM_WAKE_LOCK))
        {
            m_wakelock = m_powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, TAG);
        }
    }

    public PowerManager.WakeLock getWakeLock()
    {
        return m_wakelock;
    }
}
