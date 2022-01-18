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

class DinnerActivity : AppCompatActivity() {

    var dinnerList: ArrayList<String> ?= ArrayList()
    lateinit var dinnerListView: ListView

    var saladList: ArrayList<String> ?= ArrayList()
    lateinit var saladListView: ListView

    lateinit var dinnerAdapter : ArrayAdapter<String>
    lateinit var saladAdapter: ArrayAdapter<String>

    lateinit var orderButton: Button

    lateinit var selectedSalad: ArrayList<String>
    lateinit var selectedDinner: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dinner)

        init()

        saladAdapter = ArrayAdapter(applicationContext, android.R.layout.simple_list_item_single_choice, saladList!!)
        saladListView?.adapter = saladAdapter
        saladListView.choiceMode = ListView.CHOICE_MODE_SINGLE
        setCurry()

        orderButton.setOnClickListener() {
            checkSelections()
        }

    }

    companion object {
        const val FOOD = "selectedFood"
    }

    fun init() {
        dinnerListView = findViewById(R.id.curryList)
        saladListView = findViewById(R.id.spiceList)
        orderButton = findViewById(R.id.curryOrderButton)
        populateExtras()
    }

    //Pull the meats from the database
    private fun setCurry() {
        //Pull the information from the .php script for categories
        //Prevents unauthorized use of the database
        val queue = Volley.newRequestQueue(this)
        val url = "http://192.168.1.51/restaurantQuery/getDinnerTypes.php"

        val jsonRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                for(i in 0 until response.length()) {
                    val toppings = response.getJSONObject(i)
                    val toppingName = toppings.getString("OptionName")
                    //println(categoryFoodName)
                    val toppingCost = toppings.getDouble("OptionCost")

                    dinnerList?.add(toppingName)
                }

                dinnerAdapter = ArrayAdapter(applicationContext, android.R.layout.simple_list_item_single_choice, dinnerList!!)
                dinnerListView?.adapter = dinnerAdapter
                dinnerListView.choiceMode = ListView.CHOICE_MODE_SINGLE
                dinnerListView.itemsCanFocus = true

            },
            Response.ErrorListener {
                println("Error connecting to php script!")
            })

        queue.add(jsonRequest)
    }

    private fun checkSelections() {
        selectedSalad = ArrayList<String>()

        //Iterate through topping list and pull selected toppings
        for(i in 0 until saladListView.count) {
            if(saladListView.isItemChecked(i)) {
                selectedSalad.add(saladListView.getItemAtPosition(i).toString())
            } else {
                selectedSalad.remove(saladListView.getItemAtPosition(i).toString())
            }
        }

        //Iterate through list and find selected size
        for(i in 0 until dinnerListView.count) {
            if(dinnerListView.isItemChecked(i)) {
                selectedDinner = dinnerListView.getItemAtPosition(i).toString()
                selectedSalad.add(dinnerListView.getItemAtPosition(i).toString())
            }
        }

        //Create Sub object from selections
        val selectedFood: Food?= Food("Dinner", selectedSalad, selectedDinner)

        val intent = Intent(this, FinalizeOrderActivity::class.java)
        intent.putExtra(CurryOptionsActivity.FOOD, selectedFood)
        startActivity(intent)

        //for(i in selectedToppings) print(i + " ")
        //println("Size is $selectedSize")
    }

    //Add the Extras
    fun populateExtras() {
        saladList?.add("Ranch")
        saladList?.add("French")
        saladList?.add("Oil and Vinegar")
    }

}