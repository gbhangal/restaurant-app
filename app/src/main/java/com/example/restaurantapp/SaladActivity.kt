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

class SaladActivity : AppCompatActivity() {

    var saladList: ArrayList<String> ?= ArrayList()
    lateinit var saladListView: ListView

    var dressingList: ArrayList<String> ?= ArrayList()
    lateinit var dressingListView: ListView

    var sizeList: ArrayList<String> ?= ArrayList()
    lateinit var sizeListView: ListView

    lateinit var dinnerAdapter : ArrayAdapter<String>
    lateinit var saladAdapter: ArrayAdapter<String>
    lateinit var sizeAdapter: ArrayAdapter<String>

    lateinit var orderButton: Button

    lateinit var selectedDressing: ArrayList<String>
    lateinit var selectedSalad: String
    lateinit var selectedSize: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_salad)

        init()

        sizeAdapter = ArrayAdapter(applicationContext, android.R.layout.simple_list_item_single_choice, sizeList!!)
        sizeListView?.adapter = sizeAdapter
        sizeListView.choiceMode = ListView.CHOICE_MODE_SINGLE
        sizeListView.setItemChecked(0, true)

        saladAdapter = ArrayAdapter(applicationContext, android.R.layout.simple_list_item_single_choice, dressingList!!)
        dressingListView?.adapter = saladAdapter
        dressingListView.choiceMode = ListView.CHOICE_MODE_SINGLE
        dressingListView.setItemChecked(0, true)

        setCurry()

        orderButton.setOnClickListener() {
            checkSelections()
        }

    }

    companion object {
        const val FOOD = "selectedFood"
    }

    fun init() {
        saladListView = findViewById(R.id.curryList)
        dressingListView = findViewById(R.id.spiceList)
        sizeListView = findViewById(R.id.sizeList)
        orderButton = findViewById(R.id.curryOrderButton)
        populateExtras()
        populateSize()
    }

    //Pull the meats from the database
    private fun setCurry() {
        //Pull the information from the .php script for categories
        //Prevents unauthorized use of the database
        val queue = Volley.newRequestQueue(this)
        val url = "http://192.168.1.51/restaurantQuery/getSaladTypes.php"

        val jsonRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                for(i in 0 until response.length()) {
                    val toppings = response.getJSONObject(i)
                    val toppingName = toppings.getString("OptionName")
                    //println(categoryFoodName)
                    val toppingCost = toppings.getDouble("OptionCost")

                    saladList?.add(toppingName)
                }

                dinnerAdapter = ArrayAdapter(applicationContext, android.R.layout.simple_list_item_single_choice, saladList!!)
                saladListView?.adapter = dinnerAdapter
                saladListView.choiceMode = ListView.CHOICE_MODE_SINGLE
                saladListView.itemsCanFocus = true
                saladListView.setItemChecked(0, true)

            },
            Response.ErrorListener {
                println("Error connecting to php script!")
            })

        queue.add(jsonRequest)
    }

    private fun checkSelections() {
        selectedDressing = ArrayList<String>()

        //Iterate through dressing list and pull selected dressing
        for(i in 0 until dressingListView.count) {
            if(dressingListView.isItemChecked(i)) {
                selectedDressing.add(dressingListView.getItemAtPosition(i).toString())
            } else {
                selectedDressing.remove(dressingListView.getItemAtPosition(i).toString())
            }
        }

        //Iterate through list and find selected salad
        for(i in 0 until saladListView.count) {
            if(saladListView.isItemChecked(i)) {
                selectedSalad = saladListView.getItemAtPosition(i).toString()
                selectedDressing.add(saladListView.getItemAtPosition(i).toString())
            }
        }

        //Iterate through list and find selected salad
        for(i in 0 until sizeListView.count) {
            if(sizeListView.isItemChecked(i)) {
                selectedSize = sizeListView.getItemAtPosition(i).toString()
                selectedDressing.add(sizeListView.getItemAtPosition(i).toString())
            }
        }

        //Create Sub object from selections
        val selectedFood: Food?= Food("Salad", selectedDressing, selectedSalad)

        val intent = Intent(this, FinalizeOrderActivity::class.java)
        intent.putExtra(SaladActivity.FOOD, selectedFood)
        startActivity(intent)

        //for(i in selectedToppings) print(i + " ")
        //println("Size is $selectedSize")
    }

    //Add the Extras
    fun populateExtras() {
        dressingList?.add("Ranch")
        dressingList?.add("French")
        dressingList?.add("Oil and Vinegar")
    }

    fun populateSize() {
        sizeList?.add("Small")
        sizeList?.add("Large")
    }

}