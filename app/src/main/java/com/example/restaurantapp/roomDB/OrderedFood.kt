package com.example.restaurantapp.roomDB

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "orderedFood")
data class OrderedFood(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val foodName: String,
    val toppings: List<String>
)