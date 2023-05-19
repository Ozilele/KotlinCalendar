package com.example.calendar_app

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.CalendarView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity(), CalendarAdapter.onItemListener {

  private lateinit var recyclerView: RecyclerView
  private lateinit var monthYearView : TextView

  companion object {
    var selectedDate : LocalDate = LocalDate.now()
    fun formatDate(date : LocalDate): String {
      val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")
      return formatter.format(date)
    }
    fun formatEventDate(date : LocalDate) : String {
      val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
      return formatter.format(date)
    }

    fun formatTime(time : LocalTime) : String {
      val formatter = DateTimeFormatter.ofPattern("hh:mm:ss")
      return formatter.format(time)
    }

  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    recyclerView = findViewById(R.id.recyclerView)
    monthYearView = findViewById(R.id.monthYearView)
    AppDatabase.getDb(this.applicationContext)
//    selectedDate = LocalDate.now()
    monthYearView.text = formatDate(selectedDate)
    setMonthViewLayout()
  }

  fun getDaysInMonth(date: LocalDate) : ArrayList<LocalDate?> {
    var daysInMonthList = arrayListOf<LocalDate?>()
    val yearMonth = YearMonth.from(date)
    val daysInMonth = yearMonth.lengthOfMonth()

    val firstDayOfMonth = selectedDate.withDayOfMonth(1) // first day of month
    val dayOfWeek = firstDayOfMonth.dayOfWeek.value // from 1 to 7

    for (i in 1 until 43) {
      if(i < dayOfWeek || i > daysInMonth + dayOfWeek) {
        daysInMonthList.add(null)
      } else if(i < daysInMonth + dayOfWeek) {
        val localDate = LocalDate.of(selectedDate.year, selectedDate.month, ( (i + 1) - dayOfWeek))
//        daysInMonthList.add(( (i + 1) - dayOfWeek).toString())
        daysInMonthList.add(localDate)
      }
    }
    return daysInMonthList
  }

  fun setMonthViewLayout() {
    monthYearView.text = formatDate(selectedDate)
    val daysInMonth = getDaysInMonth(selectedDate) // getting list
    val calendarAdapter = CalendarAdapter(daysInMonth, this)

    recyclerView.layoutManager = GridLayoutManager(this, 7)
    recyclerView.adapter = calendarAdapter
  }

  fun handleNextMonth(view: View) {
    selectedDate = selectedDate.plusMonths(1)
    setMonthViewLayout()
  }

  fun handlePreviousMonth(view: View) {
    selectedDate = selectedDate.minusMonths(1)
    setMonthViewLayout()
  }

  override fun onItemClick(position: Int, date: LocalDate) {
//    var s = "Hejo"
//    var dataToBeSent = date;
    if(date != null) {
      selectedDate = date
      var myIntent = Intent(this, WeekView::class.java)
      startActivity(myIntent)
    }
  }

}