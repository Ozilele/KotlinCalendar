package com.example.calendar_app

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [EventItem::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
  abstract fun eventItemDao(): EventItemDao

  companion object { // one instance of the db object(singleton pattern)
    @Volatile
    private var INSTANCE : AppDatabase? = null

    fun getDb(context: Context) : AppDatabase {
      val tmpInstance = INSTANCE
      if(tmpInstance != null) {
        return tmpInstance
      }
      synchronized(this) { // single operation handling then next thread
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "app_database"
        ).build()
        INSTANCE = instance
        return instance
      }
    }
  }
}