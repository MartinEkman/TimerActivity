package com.example.timeractivity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import util.PrefUtil

class TimerExpiredReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        //TODO: show notification
       PrefUtil.setTimerState(MainActivity.TimerState.Stopped, context)
        PrefUtil.setAlarmSetTime(0, context)
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast. TODO("TimerExpiredReceiver.onReceive() is not implemented")
    }
}
