package com.example.calendar_app

import androidx.room.*
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface EventItemDao {

  @Query("SELECT * FROM events_table ORDER BY timeString")
  fun getAllEvents(): List<EventItem>

//  @Query("SELECT * FROM events_table WHERE dateString = :date")
//  suspend fun getEventsForDay(date: String): List<EventItem> // suspend - coure routines

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  suspend fun insertNewEvent(eventItem: EventItem)

//  @Delete("DELETE FROM events_table WHERE id = :eventID")
//  suspend fun deleteEvent()

    @Query("DELETE FROM events_table WHERE id = :eventID")
    suspend fun deleteEvent(eventID : Int?)
//  @Query("DELETE FROM events_table")
//  suspend fun deleteAllEventsForDay(list: List<EventItem>)

}