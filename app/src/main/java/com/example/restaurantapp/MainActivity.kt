package com.example.restaurantapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.room.Room
import com.example.restaurantapp.roomDB.OrderedFoodDatabase
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Listen for button press on orderButton
        orderButton.setOnClickListener() {
            moveToOrder();
        }

        val db = Room.databaseBuilder (
            applicationContext,
            OrderedFoodDatabase::class.java, "database-name"
        )   .fallbackToDestructiveMigration()
            .build()

        thread() {
            db.clearAllTables()
        }


    }

    //Changes to Order Activity screen
    private fun moveToOrder() {
        val intent = Intent(this, OrderActivity::class.java)
        startActivity(intent)
    }

}