package com.shaikot.notebook.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.shaikot.notebook.dao.NotesDao
import com.shaikot.notebook.model.Notes

@Database(entities = [Notes::class], version = 1, exportSchema = false)
abstract class NotesDatabase: RoomDatabase() {

    abstract fun myNotesDao(): NotesDao

    companion object {
        var INSTANCE: NotesDatabase? = null
        fun getDatabaseInstance(context: Context):NotesDatabase{
            val tempInstance = INSTANCE
            if(tempInstance!=null){
                return tempInstance
            }
            synchronized(this){
                val roomDatabaseInstance = Room.databaseBuilder(context,
                    NotesDatabase::class.java,"Notes")
                    .allowMainThreadQueries()
                    .build()

                INSTANCE = roomDatabaseInstance
                return roomDatabaseInstance
            }
        }
    }


}