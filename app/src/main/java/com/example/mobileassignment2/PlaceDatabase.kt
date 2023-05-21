package com.example.mobileassignment2

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database (entities = arrayOf(Place::class), version = 2, exportSchema = false)
abstract class PlaceDatabase: RoomDatabase() {
    abstract fun placeDao(): PlaceDao

    companion object{
        //Singleton prevents multiple instances of database opening at the same time
        @Volatile
        private var INSTANCE: PlaceDatabase? = null

        fun getDatabase(context: Context) : PlaceDatabase{
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }

            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PlaceDatabase::class.java,
                    "place_db"
                ).build()

                INSTANCE = instance
                return instance
            }
        }
    }
}