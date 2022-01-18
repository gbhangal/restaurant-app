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


class PizzaOptionsActivity : AppCompatActivity() {

    var sizeList: ArrayList<String> ?= ArrayList()
    lateinit var sizeListView: ListView

    var toppingList: ArrayList<String> ?= ArrayList()
    lateinit var toppingListView: ListView

    lateinit var sizeAdapter: ArrayAdapter<String>
    lateinit var toppingAdapter: ArrayAdapter<String>
    lateinit var orderButton: Button

    lateinit var selectedToppings: ArrayList<String>
    lateinit var selectedSize: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pizza_options)
        init()

        //populateToppings()

        sizeAdapter = ArrayAdapter(applicationContext, android.R.layout.simple_list_item_single_choice, sizeList!!)
        sizeListView?.adapter = sizeAdapter
        sizeListView.setItemChecked(0, true)
        setToppings()

        orderButton.setOnClickListener() {
            checkSelections()
        }

    }

    companion object {
        const val FOOD = "selectedFood"
    }

    private fun checkSelections() {
        selectedToppings = ArrayList<String>()

        //Iterate through topping list and pull selected toppings
        for(i in 0 until toppingListView.count) {
            if(toppingListView.isItemChecked(i)) {
                selectedToppings.add(toppingListView.getItemAtPosition(i).toString())
            } else {
                selectedToppings.remove(toppingListView.getItemAtPosition(i).toString())
            }
        }

        //Iterate through list and find selected size
        for(i in 0 until sizeListView.count) {
            if(sizeListView.isItemChecked(i)) {
                selectedSize = sizeListView.getItemAtPosition(i).toString()
                selectedToppings.add(selectedSize)
            }
        }

        //Create Pizza object from selections
        val selectedFood: Food ?= Food("Pizza", selectedToppings, selectedSize)

        val intent = Intent(this, FinalizeOrderActivity::class.java)
        intent.putExtra(FOOD,selectedFood)
        startActivity(intent)

        //for(i in selectedToppings) print(i + " ")
        //println("Size is $selectedSize")
    }


    private fun setToppings() {
        //Pull the information from the .php script for categories
        //Prevents unauthorized use of the database
        val queue = Volley.newRequestQueue(this)
        val url = "http://192.168.1.51/restaurantQuery/getFoodOptions.php"

        val jsonRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                for(i in 0 until response.length()) {
                    val toppings = response.getJSONObject(i)
                    val toppingName = toppings.getString("OptionName")
                    //println(categoryFoodName)
                    val toppingCost = toppings.getDouble("OptionCost")

                    toppingList?.add(toppingName)
                }

                toppingAdapter = ArrayAdapter(applicationContext, android.R.layout.simple_list_item_multiple_choice, toppingList!!)
                toppingListView?.adapter = toppingAdapter
                toppingListView.choiceMode = ListView.CHOICE_MODE_MULTIPLE
                toppingListView.itemsCanFocus = true

            },
            Response.ErrorListener {
                println("Error connecting to php script!")
            })

        queue.add(jsonRequest)
    }


    

    fun init() {
        sizeListView = findViewById(R.id.sizeList)
        toppingListView = findViewById(R.id.toppingList)
        orderButton = findViewById(R.id.orderButton)
        populateSize()
    }

    //Add the sizes
    fun populateSize() {
        sizeList?.add("Small")
        sizeList?.add("Medium")
        sizeList?.add("Large")
    }

    /*
    //temporary GUI testing data
    fun populateToppings() {
        toppingList?.add("Pepperoni")
        toppingList?.add("Sausage")
        toppingList?.add("Mushroom")
        toppingList?.add("Onion")
        toppingList?.add("Green Pepper")
        toppingList?.add("Black Olives")
        toppingList?.add("Hamburger")
        toppingList?.add("Canadian Bacon")
        toppingList?.add("Ham")
        toppingList?.add("Sliced Tomatoes")
        toppingList?.add("Pineapple")
        toppingList?.add("Banana Pepper")
        toppingList?.add("Jalapeno")
    }
    */


}
