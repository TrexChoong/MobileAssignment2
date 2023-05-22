package com.example.mobileassignment2

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.launch

class PlaceViewModel (application: Application): AndroidViewModel(application) {
    //LiveData gives us updated contacts when they change
    var placeList : LiveData<List<Place>>
    private val repository: PlaceRepository
    var selectedIndex: Int = -1

    init {
        val placeDao = PlaceDatabase.getDatabase(application).placeDao()
        repository = PlaceRepository(placeDao)
        placeList = repository.allPlaces
    }

    fun addContact(place: Place) = viewModelScope.launch{
        repository.add(place)
    }

    fun deleteContact(place: Place) = viewModelScope.launch {
        repository.delete(place)
    }

    fun deleteAll() = viewModelScope.launch {
        repository.deleteAll()
    }

    fun updateContact(place: Place) = viewModelScope.launch {
        repository.update(place)
    }

    fun uploadContact(id: String) = viewModelScope.launch {
        repository.uploadPlace(id)
    }
    fun toJson(): String {
        val mapper = ObjectMapper()
        val writer = mapper.writerWithDefaultPrettyPrinter()

        return try {
            writer.writeValueAsString(this)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}
