package com.example.calendar_app

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "events_table")
data class EventItem(
  @PrimaryKey(autoGenerate = true) var id: Int?,
  @ColumnInfo(name = "evName") var evName : String,
  @ColumnInfo(name = "dateString") var dateString: String?,
  @ColumnInfo(name = "timeString") var timeString: String?,
) {

}