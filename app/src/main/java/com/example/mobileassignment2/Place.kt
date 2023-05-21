package com.example.mobileassignment2

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "place")
data class Place (val vicinity: String, @PrimaryKey val name: String, val date_expired: String) {
    override fun toString(): String {
        return "{name : $name," +
                "vicinity: $vicinity," +
                "date_expired : $date_expired}"
    }
}
