package com.example.restaurantapp.roomDB

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface OrderedFoodDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addFood(orderedFood: OrderedFood)

    @Query("SELECT * FROM OrderedFood")
    fun readCurrFood(): List<OrderedFood>

    @Query("SELECT * FROM OrderedFood")
    fun readFood(): LiveData<List<OrderedFood>>
}