package com.example.restaurantapp.roomDB

import androidx.room.TypeConverter
import javax.xml.transform.Source

class Converters {
    @TypeConverter
    fun fromList(toppingList: List<String>) : String {
        return toppingList.joinToString(prefix = "[", separator = ",", postfix = "]")
    }

    @TypeConverter
    fun  toList(toppingString: String): List<String> {
        val finalList = mutableListOf<String>()
        val trimmedToppingString = toppingString.substring(1, toppingString.length - 1)
        val array = trimmedToppingString.split(",")

        for(s in array) {
            finalList.add(s)
        }
        return finalList

    }
}
