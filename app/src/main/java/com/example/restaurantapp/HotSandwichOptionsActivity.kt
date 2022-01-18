package com.example.restaurantapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.restaurantapp.food.Food

class HotSandwichOptionsActivity : AppCompatActivity() {

    var meatList: ArrayList<String> ?= ArrayList()
    lateinit var meatListView: ListView

    var extrasList: ArrayList<String> ?= ArrayList()
    lateinit var extrasListView: ListView

    lateinit var meatAdapter : ArrayAdapter<String>
    lateinit var extrasAdapter: ArrayAdapter<String>

    lateinit var orderButton: Button

    lateinit var selectedToppings: ArrayList<String>
    lateinit var selectedSize: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hot_sandwich_options)

        init()

        extrasAdapter = ArrayAdapter(applicationContext, android.R.layout.simple_list_item_multiple_choice, extrasList!!)
        extrasListView?.adapter = extrasAdapter
        extrasListView.choiceMode = ListView.CHOICE_MODE_MULTIPLE
        setMeats()

        orderButton.setOnClickListener() {
            checkSelections()
        }

    }

    companion object {
        const val FOOD = "selectedFood"
    }

    fun init() {
        meatListView = findViewById(R.id.hotMeatList)
        extrasListView = findViewById(R.id.hotExtrasList)
        orderButton = findViewById(R.id.hotOrderButton)
        populateExtras()
    }

    //Pull the meats from the database
    private fun setMeats() {
        //Pull the information from the .php script for categories
        //Prevents unauthorized use of the database
        val queue = Volley.newRequestQueue(this)
        val url = "http://192.168.1.51/restaurantQuery/getHotSubOptions.php"

        val jsonRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                for(i in 0 until response.length()) {
                    val toppings = response.getJSONObject(i)
                    val toppingName = toppings.getString("OptionName")
                    //println(categoryFoodName)
                    val toppingCost = toppings.getDouble("OptionCost")

                    meatList?.add(toppingName)
                }

                meatAdapter = ArrayAdapter(applicationContext, android.R.layout.simple_list_item_single_choice, meatList!!)
                meatListView?.adapter = meatAdapter
                meatListView.choiceMode = ListView.CHOICE_MODE_MULTIPLE
                meatListView.itemsCanFocus = true

            },
            Response.ErrorListener {
                println("Error connecting to php script!")
            })

        queue.add(jsonRequest)
    }

    private fun checkSelections() {
        selectedToppings = ArrayList<String>()

        //Iterate through topping list and pull selected toppings
        for(i in 0 until extrasListView.count) {
            if(extrasListView.isItemChecked(i)) {
                selectedToppings.add(extrasListView.getItemAtPosition(i).toString())
            } else {
                selectedToppings.remove(extrasListView.getItemAtPosition(i).toString())
            }
        }

        //Iterate through list and find selected size
        for(i in 0 until meatListView.count) {
            if(meatListView.isItemChecked(i)) {
                selectedSize = meatListView.getItemAtPosition(i).toString()
                selectedToppings.add(meatListView.getItemAtPosition(i).toString())
            }
        }

        //Create Sub object from selections
        val selectedFood: Food?= Food("HotSub", selectedToppings, selectedSize)

        val intent = Intent(this, FinalizeOrderActivity::class.java)
        intent.putExtra(HotSandwichOptionsActivity.FOOD, selectedFood)
        startActivity(intent)

        //for(i in selectedToppings) print(i + " ")
        //println("Size is $selectedSize")
    }

    //Add the Extras
    fun populateExtras() {
        extrasList?.add("Cheese")
        extrasList?.add("Pepper")
        extrasList?.add("Mushroom")
        extrasList?.add("Grilled Onion")
        extrasList?.add("Sauce")
    }


}