package com.example.timeractivity

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_first.*
import util.PrefUtil
import java.util.*

class MainActivity : AppCompatActivity() {


    //TimerState default value 0 är Stopped. Pause är 1 och Running 2. Antar jag?
    enum class TimerState{
        Stopped, Pause, Running

    }

    companion object {
        fun setAlarm(context: Context, nowSeconds: Long,
                     secondsRemaining:Long): Long{
            val wakeUpTime =(nowSeconds + secondsRemaining) * 1000
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, TimerExpiredReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0 , intent, 0)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, wakeUpTime, pendingIntent)
            PrefUtil.setAlarmSetTime(nowSeconds, context)
            return wakeUpTime

        }
        fun removeAlarm(context: Context){
            val intent = Intent(context, TimerExpiredReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0 , intent, 0)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
            PrefUtil.setAlarmSetTime(0, context)
        }

        val nowSeconds: Long get() = Calendar.getInstance().timeInMillis / 1000
    }


    //Egen skrivna variabler
    private lateinit var timer: CountDownTimer
    private var timerLengthSeconds = 0L
    private var timerState = TimerState.Stopped
    private var secondsRemaining = 0L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        //Genom supportActionBar?. Så ändrar du actionbaren högst upp. 
        supportActionBar?.setIcon(R.drawable.ic_timer)
        supportActionBar?.title="       Timer"

        //v för View och starTimer() är en metod som man definierar själv, även updateButtons()
        // Detta händer när du klickar på Start.
        fab_start.setOnClickListener{v ->
            startTimer()
            timerState = TimerState.Running
            updateButtons()
        }
        fab_pause.setOnClickListener{v ->
        timer.cancel()
            timerState = TimerState.Pause

        }

        fab_stop.setOnClickListener{v ->
            timer.cancel()
            timerState = TimerState.Stopped
            onTimerFinished()
        }

    }
        //initTimer där init verkar betyda att man initierar någonting och i detta fall Timer.
        //Timer används för att för att kunna exekvera saker i bakrunden?
    override fun onResume() {
        super.onResume()
        initTimer()

            removeAlarm(this)
            //TODO:  also hide notification
    }
    //Denna funktion aktiveras precis innan/då man går till bakrunden eller liknande.
    override fun onPause() {
        super.onPause()
        if(timerState == TimerState.Running){
            timer.cancel()
            val wakeUpTime = setAlarm(this, nowSeconds, secondsRemaining)
            //TODO: show notification
        }
        else if(timerState == TimerState.Pause){
            //TODO: show notification

        }
        //timerLengthSeconds. vilket är den nuvarande/nutids timerLengthSeconds
        //Här sparas exempelvis datan/sekunderna/tiden innan du pausar
        //Från att den pausas till att den återtas så kallas den på dessa
        PrefUtil.setPreviousTimerLengthSeconds(timerLengthSeconds,this)
        PrefUtil.setSecondsRemaining(secondsRemaining, this)
        PrefUtil.setTimerState(timerState,this)

    }
        //OM timern stoppas så går den till NewTimerLength
        //ANNARS den pausas så går den tillbaka till PreviousTimerLength
    private fun initTimer(){
        timerState = PrefUtil.getTimerState(this)
        if(timerState == TimerState.Stopped)
            setNewTimerLength()
        else
            setPreviousTimerLength()

                //OM TimerState.Running kör eller
                //OM TimerState.Pause körs
                //Annars om TimerState blir
               // stoppad så kallar vi på timerLengthSeconds
            secondsRemaining = if (timerState == TimerState.Running ||
                    timerState == TimerState.Pause)
                PrefUtil.getSecondsRemaining(this)
            else
                //Om den kör timern eller pausar den så kallar
                //vi på timerLengthSeconds som på rad 88-91 (if satsen)
                //bestämmer om den ska gå tillbaka eller till den nya funktionen
                timerLengthSeconds

            val alarmSetTime = PrefUtil.getAlarmSetTime(this)
            if(alarmSetTime > 0)
                secondsRemaining -= nowSeconds - alarmSetTime

            if (secondsRemaining <= 0)
                onTimerFinished()
            else if (timerState ==TimerState.Running)
                startTimer()
                updateButtons()
                updateCountdownUI()
    }

    private fun onTimerFinished(){
        timerState = TimerState.Stopped

        setNewTimerLength()

        progress_countdown.progress= 0

        PrefUtil.setSecondsRemaining(timerLengthSeconds, this)
        secondsRemaining = timerLengthSeconds

        updateButtons()
        updateCountdownUI()
    }

    private fun startTimer(){
        timerState = TimerState.Running

        timer = object : CountDownTimer(secondsRemaining * 1000, 1000){
            override fun onFinish() = onTimerFinished()
            override fun onTick(millisUnilFinished: Long) {
                secondsRemaining = millisUnilFinished / 1000
                updateCountdownUI()
            }
        }.start()
    }

    private fun setNewTimerLength(){
        val lengthInMinutes = PrefUtil.getTimerLength(this)
        timerLengthSeconds = (lengthInMinutes * 60L)
        progress_countdown.max = timerLengthSeconds.toInt()

    }

    private fun setPreviousTimerLength(){
        timerLengthSeconds = PrefUtil.getPreviousTimerLengthSeconds(this)
        progress_countdown.max = timerLengthSeconds.toInt()

    }


    private fun updateCountdownUI(){
        val minutesUntilFinished = secondsRemaining/60
        val secondsInMinuteUntilFinished = secondsRemaining - minutesUntilFinished * 60
        val secondsStr = secondsInMinuteUntilFinished.toString()
        textView_countdown.text = "$minutesUntilFinished:${
        if (secondsStr.length ==2) secondsStr
        else "0" + secondsStr}"
        progress_countdown.progress = (timerLengthSeconds - secondsRemaining).toInt()
    }

    private fun updateButtons(){
        when(timerState){
            TimerState.Running ->{
                fab_start.isEnabled = false
                fab_pause.isEnabled = true
                fab_stop.isEnabled = true
            }
            TimerState.Stopped -> {
                fab_start.isEnabled = true
                fab_pause.isEnabled = false
                fab_stop.isEnabled = false
            }
            TimerState.Pause -> {
                fab_start.isEnabled = true
                fab_pause.isEnabled = false
                fab_stop.isEnabled = true
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
