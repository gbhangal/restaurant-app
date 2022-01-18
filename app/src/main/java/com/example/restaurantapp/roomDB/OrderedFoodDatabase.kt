package com.example.restaurantapp.roomDB

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = arrayOf(OrderedFood::class), version = 3)
@TypeConverters(Converters::class)
abstract class OrderedFoodDatabase : RoomDatabase() {
    abstract fun OrderedFoodDao(): OrderedFoodDao
}