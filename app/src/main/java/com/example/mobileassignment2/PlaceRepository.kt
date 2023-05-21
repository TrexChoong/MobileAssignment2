package com.example.mobileassignment2

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class PlaceRepository(private val placeDao: PlaceDao){
    //Room execute all queries on a separate thread
    val allPlaces: LiveData<List<Place>> = placeDao.getAllPlace()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun add(contact: Place){
        placeDao.insert(contact)
    }

    @WorkerThread
    suspend fun delete(contact: Place){
        placeDao.delete(contact)
    }

    @WorkerThread
    suspend fun update(contact: Place){
        placeDao.update(contact)
    }

    @WorkerThread
    suspend fun deleteAll(){
        placeDao.deleteAll()
    }

//    fun uploadPlace(id: String){
//        if(allPlaces.isInitialized){
//            if(!allPlaces.value.isNullOrEmpty()){
//                val database = Firebase.database("https://findmyrahmah-e29bf-default-rtdb.asia-southeast1.firebasedatabase.app/").reference
//                allPlaces.value!!.forEach{
//                    database.child(id).child(it.vicinity).setValue(it)
//                }
//            }
//        }
//    }
}