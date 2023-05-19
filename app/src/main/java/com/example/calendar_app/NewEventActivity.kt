package com.example.calendar_app

import android.app.Activity
import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.res.Configuration
import android.icu.util.Calendar
import android.media.metrics.Event
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.util.*

class NewEventActivity : AppCompatActivity() {

  lateinit var eventName : EditText
  lateinit var eventDate : TextView
  private lateinit var appDB : AppDatabase
  lateinit var eventTime : Button
  lateinit var time : LocalTime
  var hour : Int = LocalTime.now().hour
  var min: Int = LocalTime.now().minute

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
      setContentView(R.layout.activity_new_event)
    } else {
      setContentView(R.layout.activity_new_event_land);
    }
    appDB = AppDatabase.getDb(this.applicationContext)
    eventName = findViewById(R.id.eventName)
    eventDate = findViewById(R.id.eventDate)
    eventTime = findViewById(R.id.eventTime)
    time = LocalTime.now()
    eventDate.text = "Data: " + MainActivity.formatEventDate(MainActivity.selectedDate)
    eventTime.text = "Czas: " + MainActivity.formatTime(time)
  }

  fun handleAddEvent(view: View) {
    var evName = eventName.text.toString()
    if(eventName.toString().isNotEmpty() && eventTime.toString().isNotEmpty()) {
      val timeString = hour.toString() + ":" + min.toString().padStart(2, '0')
      val newEventItem = EventItem(null, evName, MainActivity.selectedDate.toString(), timeString)

      GlobalScope.launch(Dispatchers.IO) {
        appDB.eventItemDao().insertNewEvent(newEventItem)
      }
    } else {
      println("Error")
    }

    val weekIntent = Intent(this, WeekView::class.java)
    setResult(Activity.RESULT_OK, weekIntent)
    finish() // back to week activity
  }

  fun popTimePicker(view: View) {
    val timePickerListener = TimePickerDialog.OnTimeSetListener{_, hourOfDay, minute ->
      hour = hourOfDay
      min = minute
      eventTime.text = String.format(Locale.getDefault(), "%02d:%02d", hour, min)
    }
    val style = AlertDialog.THEME_HOLO_DARK
    val timePickerDialog = TimePickerDialog(
      this,
      style,
      timePickerListener,
      hour,
      min,
      true
    )
    timePickerDialog.setTitle("Select Time:")
    timePickerDialog.show()
  }

}