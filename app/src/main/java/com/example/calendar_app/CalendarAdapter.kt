package com.example.calendar_app

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate

class CalendarAdapter(var calendarDays: ArrayList<LocalDate?>, var listener : onItemListener) : RecyclerView.Adapter<CalendarViewHolder>() {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
    val dayView = LayoutInflater.from(parent.context).inflate(R.layout.calendar_item, parent, false)
    if(calendarDays.size > 15) {
      dayView.layoutParams.height = (parent.height * 0.1666666).toInt()
    } else {
      dayView.layoutParams.height = parent.height
    }

    return CalendarViewHolder(dayView, listener)
  }

  override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
    var date = calendarDays[position]
    if(date == null) {
      holder.dayOfMonth.text = ""
    } else {
      holder.dayOfMonth.text = date?.dayOfMonth.toString()
      if(date.equals(MainActivity.selectedDate)) {
        holder.dayOfMonth.setBackgroundResource(R.drawable.rounded_corner_view)
        holder.dayOfMonth.setTextColor(Color.WHITE)
      }
    }
    if(date != null) {
      holder.bind(position, date)
    }

  }

  override fun getItemCount(): Int {
    return calendarDays.size
  }

  interface onItemListener {
    fun onItemClick(position: Int, date : LocalDate)
  }
}

class CalendarViewHolder(itemView: View, listener: CalendarAdapter.onItemListener) : RecyclerView.ViewHolder(itemView) {
  var dayOfMonth : TextView = itemView.findViewById(R.id.calendarItem)
  var dayOfMonthView : ConstraintLayout = itemView.findViewById(R.id.calendarItemView)
  lateinit var listener : CalendarAdapter.onItemListener

  init {
    this.listener = listener
  }

  fun bind(position: Int, date: LocalDate) {
    dayOfMonth.setOnClickListener { view ->
      listener.onItemClick(position, date)
    }
  }
}