package com.example.mobileassignment2

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PlaceDao {
    @Query("SELECT * FROM place ORDER BY name ASC")
    fun getAllPlace(): LiveData<List<Place>>

    @Query("DELETE FROM place")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(place: Place)

    @Delete
    suspend fun delete(place: Place)

    @Update
    suspend fun update(place: Place)
}