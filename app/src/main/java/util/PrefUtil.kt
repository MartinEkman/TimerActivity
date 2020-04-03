package util

import android.content.Context
import android.preference.PreferenceManager
import com.example.timeractivity.MainActivity

class PrefUtil {
        companion object {
                                //Använder Context för att "extract a value from preferences
                                                // :Int menas att den ska retunera en Integer
            fun getTimerLength(context: Context): Int{
                //Detta är endast en placeholder funktion
                //Retunerar 1 min och denna place holder ska tas bort/ändras så vi kan
                //sätta längden/tiden på timern.
                return 1
            }
            //Detta är ID som vi ska använda i preferences för att-
            // identifiera värdena för preferences?
            //Detta är en string. De är också bra att inkludera ett package namn?
            //com.resocoder är förmodligen bara en massa reklam för hans egen kanal. kan heta vad som helst i guess...

            private const val PREVIOUS_TIMER_LENGTH_SECONDS_ID = "com.resocoder.timer.previous_timer_length"

            fun getPreviousTimerLengthSeconds(context: Context): Long{
                //Denna funktion används utifall du sätter timern på 20 min och ändrar till 20 min medans den kör
                //så stannar den och ändrar tiden så att du inte ändrar medans den kör för då chrashar det antar jag?
                // Därför gör man denna funktion som en "Future" timer så man inte ändrar på den som körs.

                val preferences = PreferenceManager.getDefaultSharedPreferences(context)
                return preferences.getLong(PREVIOUS_TIMER_LENGTH_SECONDS_ID, 0)

            }


            fun setPreviousTimerLengthSeconds(seconds: Long, context: Context){
                val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
                editor.putLong(PREVIOUS_TIMER_LENGTH_SECONDS_ID, seconds)
                editor.apply()

            }
                    //För att hålla koll på Timer State
                private const val TIMER_STATE_ID = "com.rescoder.timer.timer_state"

            fun getTimerState(context: Context): MainActivity.TimerState{
                val preferences = PreferenceManager.getDefaultSharedPreferences(context)
                val ordinal = preferences.getInt(TIMER_STATE_ID, 0)
                return MainActivity.TimerState.values()[ordinal]
            }

            fun setTimerState(state: MainActivity.TimerState, context: Context) {
                val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
                val ordinal = state.ordinal
                editor.putInt(TIMER_STATE_ID,ordinal)
                editor.apply()

            }
            private const val SECONDS_REMAINING_ID = "com.resocoder.timer.previous_timer_length"

            fun getSecondsRemaining(context: Context): Long{
                //Denna funktion används utifall du sätter timern på 20 min och ändrar till 20 min medans den kör
                //så stannar den och ändrar tiden så att du inte ändrar medans den kör för då chrashar det antar jag?
                // Därför gör man denna funktion som en "Future" timer så man inte ändrar på den som körs.

                val preferences = PreferenceManager.getDefaultSharedPreferences(context)
                return preferences.getLong(SECONDS_REMAINING_ID, 0)

            }


            fun setSecondsRemaining(seconds: Long, context: Context){
                val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
                editor.putLong(SECONDS_REMAINING_ID, seconds)
                editor.apply()

            }

            private const val ALARM_SET_TIME_ID = "com.rescoder.timer.backgrounded_time"

            fun getAlarmSetTime(context: Context): Long{
                val preferences = PreferenceManager.getDefaultSharedPreferences(context)
                return preferences.getLong(ALARM_SET_TIME_ID, 0 )
            }

            fun setAlarmSetTime(time: Long, context: Context){

                val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
                editor.putLong(ALARM_SET_TIME_ID, time)
                editor.apply()

            }
        }
}