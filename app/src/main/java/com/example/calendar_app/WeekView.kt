package com.example.calendar_app

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.media.metrics.Event
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.*
import java.security.spec.ECField
import java.time.DayOfWeek
import java.time.LocalDate

class WeekView : AppCompatActivity(), CalendarAdapter.onItemListener {

  private lateinit var recyclerView: RecyclerView
  private lateinit var monthYear : TextView
  private lateinit var days : ArrayList<LocalDate?>
//  private lateinit var appDB : AppDatabase
  private lateinit var newEventBtn : FloatingActionButton
  lateinit var launcher : ActivityResultLauncher<Intent>

  companion object {
    lateinit var appDB : AppDatabase
    private lateinit var eventAdapter : EventAdapter
    private lateinit var listView : ListView
    var allEvents : List<EventItem> = emptyList()
    lateinit var eventsForDate : List<EventItem>

    suspend fun getEv() : List<EventItem> = withContext(Dispatchers.IO) {
      return@withContext appDB.eventItemDao().getAllEvents()
    }

    fun updateList(context: Context) {
      CoroutineScope(Dispatchers.Main).launch {
        allEvents = getEv()
        eventsForDate = allEvents.filter {
          it.dateString == MainActivity.selectedDate.toString()
        }
        println("$eventsForDate")
        eventAdapter.events = eventsForDate.toMutableList()
        eventAdapter.notifyDataSetChanged()
        listView.adapter = EventAdapter(context, eventsForDate.toMutableList())
      }
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
      setContentView(R.layout.activity_week_view)
    } else {
      setContentView(R.layout.activity_week_view_land);
    }
    recyclerView = findViewById(R.id.recyclerView)
    monthYear = findViewById(R.id.monthYearView)
    newEventBtn = findViewById(R.id.newEventButton)
    listView = findViewById(R.id.list_view)
    appDB = AppDatabase.getDb(this.applicationContext)

    launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
      if(result.resultCode == Activity.RESULT_OK) {
        updateForInsert()
      }
    }

    if(savedInstanceState != null) { // aktywność odtworzona po zniszczeniu, przy rotacji ekranu
      println("_____")
      val daysStr = savedInstanceState.getStringArrayList("daysData")
      days = daysStr!!.map { LocalDate.parse(it) } as ArrayList<LocalDate?>
      val calendarAdapter = CalendarAdapter(days, this@WeekView)
      monthYear.text = MainActivity.formatDate(MainActivity.selectedDate)
      recyclerView.layoutManager = GridLayoutManager(this@WeekView, 7)
      recyclerView.adapter = calendarAdapter
      setEventAdpater()
    } else {
      CoroutineScope(Dispatchers.Main).launch {
        allEvents = getEv()
        println("All events: $allEvents")
        setWeekView()
      }
    }

  }

  fun setWeekView() {
    monthYear.text = MainActivity.formatDate(MainActivity.selectedDate)
    days = getDaysInWeek(MainActivity.selectedDate)
    val calendarAdapter = CalendarAdapter(days, this)
    recyclerView.layoutManager = GridLayoutManager(this, 7)
    recyclerView.adapter = calendarAdapter
    setEventAdpater()
  }

  fun updateForInsert() {
    CoroutineScope(Dispatchers.Main).launch {
      allEvents = getEv()
      eventsForDate = allEvents.filter {
        it.dateString == MainActivity.selectedDate.toString()
      }
      println("$eventsForDate")
      eventAdapter.events = eventsForDate.toMutableList()
      eventAdapter.notifyDataSetChanged()
      listView.adapter = EventAdapter(this@WeekView, eventsForDate.toMutableList())
    }
  }


  fun getDaysInWeek(date: LocalDate) : ArrayList<LocalDate?> {
    var days = arrayListOf<LocalDate?>()
    var currDate = date
    var currDateWeekAgo = currDate.minusWeeks(1)

    while(currDate.isAfter(currDateWeekAgo)) {
      if(currDate.dayOfWeek == DayOfWeek.MONDAY) {
        break
      }
      currDate = currDate.minusDays(1)
    }
    var endDate = currDate.plusWeeks(1)

    while(currDate.isBefore(endDate)) {
      days.add(currDate)
      currDate = currDate.plusDays(1)
    }
    return days
  }

  fun handlePreviousWeek(view: View) {
    MainActivity.selectedDate = MainActivity.selectedDate.minusWeeks(1)
    setWeekView()
  }

  fun handleNextWeek(view: View) {
    MainActivity.selectedDate = MainActivity.selectedDate.plusWeeks(1)
    setWeekView()
  }

  override fun onItemClick(position: Int, date: LocalDate) {
    if (date != null) {
      MainActivity.selectedDate = date
    }
    setWeekView()
  }

  fun setEventAdpater() {
    eventsForDate = allEvents.filter {
      it.dateString == MainActivity.selectedDate.toString()
    }
    println("$eventsForDate")
//    if (!::eventAdapter.isInitialized) {
//      eventAdapter = EventAdapter(this, mutableListOf())
//    }
//    val realEvents = eventsForDate.filter { EventAdapter. }
    eventAdapter = EventAdapter(this, eventsForDate.toMutableList())
//    eventAdapter = EventAdapter(this, eventsForDate.filter { !(eventAdapter?.events?.contains(it) ?: false) }.toMutableList())
    listView.adapter = eventAdapter
  }

  fun handleNewEvent(view: View) {
    var eventIntent = Intent(this, NewEventActivity::class.java)
    launcher.launch(eventIntent)
//    startActivity(eventIntent)
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    val daysData = (recyclerView.adapter as CalendarAdapter).calendarDays
    val daysDataString = daysData.map { it.toString() }
    outState.putStringArrayList("daysData", ArrayList(daysDataString))
    // Always call the superclass so it can save the view hierarchy state.
  }

  override fun onRestoreInstanceState(savedInstanceState: Bundle) {
    // Always call the superclass so it can restore the view hierarchy.
    super.onRestoreInstanceState(savedInstanceState)

    // Restore state members from saved instance.
  }

}

