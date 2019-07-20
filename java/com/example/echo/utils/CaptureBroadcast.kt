package com.example.echo.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.example.echo.R
import com.example.echo.activities.MainActivity
import com.example.echo.fragments.SongPlayingFragment

class CaptureBroadcast : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_NEW_OUTGOING_CALL) {
            try {
                MainActivity.Statified.notificationManager?.cancel(1978)
            } catch (e: Exception) {
            }
            try {
                if (SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean) {
                    SongPlayingFragment.Statified.mediaPlayer?.pause()
                    SongPlayingFragment.Statified.playPauseButton?.setBackgroundResource(R.drawable.play_icon)
                }

            } catch (e: Exception) {
            }
        } else {
            val tm: TelephonyManager = context?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            when (tm?.callState) {
                TelephonyManager.CALL_STATE_RINGING ->
                    try {
                        try {
                            MainActivity.Statified.notificationManager?.cancel(1978)
                        } catch (e: Exception) {
                        }
                        if (SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean) {
                            SongPlayingFragment.Statified.mediaPlayer?.pause()
                            SongPlayingFragment.Statified.playPauseButton?.setBackgroundResource(R.drawable.play_icon)
                        }

                    } catch (e: Exception) {
                    }
                else -> {
                }
            }
        }
    }
}