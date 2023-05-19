package com.example.calendar_app

import android.app.Activity
import android.content.Context
import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope

import kotlinx.coroutines.launch
import java.io.Serializable
import java.time.LocalTime

class EventAdapter(context: Context, var events: MutableList<EventItem>) : ArrayAdapter<EventItem>(context, 0, events) {

  lateinit var list: MutableList<EventItem>

  companion object {
    private val deletedEvents = HashSet<EventItem>()
  }

  init {
    list = events
  }

  override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
    val event = getItem(position)

    var itemView = convertView
    if (itemView == null) {
      itemView = LayoutInflater.from(context).inflate(R.layout.event_item, parent, false)
    }

    val eventCellTV = itemView!!.findViewById<TextView>(R.id.eventCellTV)
    val eventBtn = itemView!!.findViewById<TextView>(R.id.btnDeleteEvent)

    eventBtn.setOnClickListener { view ->
      GlobalScope.launch {
        if (event != null) {
          AppDatabase.getDb(context).eventItemDao().deleteEvent(event!!.id)
          WeekView.updateList(context)
//          removeEvent(event)
          println(event!!.id)
        }
      }
    }

    val eventTitle = event!!.evName + " " +  event.timeString
    eventCellTV.text = eventTitle

//    if (deletedEvents.contains(event)) {
//      itemView.visibility = View.GONE
//      itemView.background = null
//    } else {
//      itemView.visibility = View.VISIBLE
//    }
//
    return itemView
  }

  fun removeEvent(event: EventItem) {
//    val eventIndex = events.indexOfFirst { it.id == eventId }
//    if (eventIndex != -1) {
    events.remove(event)
    deletedEvents.add(event)
    (context as Activity).runOnUiThread {
      notifyDataSetChanged()
    }
  }

}