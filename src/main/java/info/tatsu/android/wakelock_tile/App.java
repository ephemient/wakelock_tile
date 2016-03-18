package info.tatsu.android.wakelock_tile;

import android.app.Application;
import android.os.PowerManager;

public class App extends Application
{
    private static final String TAG = App.class.getPackage().getName();

    private PowerManager m_powerManager;

    @Override
    public void onCreate()
    {
        super.onCreate();
        m_powerManager = (PowerManager) getSystemService(POWER_SERVICE);
    }

    public PowerManager.WakeLock newWakeLock()
    {
        if (m_powerManager.isWakeLockLevelSupported(PowerManager.SCREEN_DIM_WAKE_LOCK))
        {
            return m_powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, TAG);
        }
        return null;
    }
}
