package info.tatsu.android.wakelock_tile

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

class WakelockQSTileService : TileService() {
    private val preferences by lazy { getSharedPreferences("QS", Context.MODE_PRIVATE) }
    private var added
        get() = preferences.getBoolean(IS_ADDED_PREFERENCE, false)
        set(value) = preferences.edit().putBoolean(IS_ADDED_PREFERENCE, value).apply()

    override fun onCreate() {
        super.onCreate()
        if (added) {
            TileService.requestListeningState(this, ComponentName(this, javaClass))
        }
    }

    override fun onTileAdded() {
        added = true
        TileService.requestListeningState(this, ComponentName(this, javaClass))
        updateTile((application as? App)?.heldAwake)
    }

    override fun onTileRemoved() {
        added = false
        startService(Intent(this, WakelockHolderService::class.java).apply {
            addCategory(Intent.ACTION_RUN)
            putExtra(WakelockHolderService.WAKELOCK_EXTRA, false)
        })
    }

    override fun onClick() {
        stopForeground(true)
        updateTile((application as? App)?.heldAwake?.let { !it }?.also {
            startService(Intent(this, WakelockHolderService::class.java).apply {
                addCategory(Intent.ACTION_RUN)
                putExtra(WakelockHolderService.WAKELOCK_EXTRA, it)
            })
        })
    }

    private fun updateTile(wakelockIsHeld: Boolean?) {
        qsTile?.run {
            state = when (wakelockIsHeld) {
                null -> Tile.STATE_UNAVAILABLE
                true -> Tile.STATE_ACTIVE
                false -> Tile.STATE_INACTIVE
            }
            updateTile()
        }
    }

    companion object {
        private const val IS_ADDED_PREFERENCE = "isAdded"
    }
}
