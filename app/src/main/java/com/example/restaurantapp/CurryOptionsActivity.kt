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

class CurryOptionsActivity : AppCompatActivity() {

    var curryList: ArrayList<String> ?= ArrayList()
    lateinit var curryListView: ListView

    var spiceList: ArrayList<String> ?= ArrayList()
    lateinit var spiceListView: ListView

    lateinit var curryAdapter : ArrayAdapter<String>
    lateinit var spiceAdapter: ArrayAdapter<String>

    lateinit var orderButton: Button

    lateinit var selectedSpice: ArrayList<String>
    lateinit var selectedCurry: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_curry_options)

        init()

        spiceAdapter = ArrayAdapter(applicationContext, android.R.layout.simple_list_item_single_choice, spiceList!!)
        spiceListView?.adapter = spiceAdapter
        spiceListView.choiceMode = ListView.CHOICE_MODE_SINGLE
        spiceListView.setItemChecked(0, true)
        setCurry()

        orderButton.setOnClickListener() {
            checkSelections()
        }

    }

    companion object {
        const val FOOD = "selectedFood"
    }

    fun init() {
        curryListView = findViewById(R.id.curryList)
        spiceListView = findViewById(R.id.spiceList)
        orderButton = findViewById(R.id.curryOrderButton)
        populateExtras()
    }

    //Pull the meats from the database
    private fun setCurry() {
        //Pull the information from the .php script for categories
        //Prevents unauthorized use of the database
        val queue = Volley.newRequestQueue(this)
        val url = "http://192.168.1.51/restaurantQuery/getCurryTypes.php"

        val jsonRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                for(i in 0 until response.length()) {
                    val toppings = response.getJSONObject(i)
                    val toppingName = toppings.getString("OptionName")
                    //println(categoryFoodName)
                    val toppingCost = toppings.getDouble("OptionCost")

                    curryList?.add(toppingName)
                }

                curryAdapter = ArrayAdapter(applicationContext, android.R.layout.simple_list_item_single_choice, curryList!!)
                curryListView?.adapter = curryAdapter
                curryListView.choiceMode = ListView.CHOICE_MODE_SINGLE
                curryListView.itemsCanFocus = true
                curryListView.setItemChecked(0, true)

            },
            Response.ErrorListener {
                println("Error connecting to php script!")
            })

        queue.add(jsonRequest)
    }

    private fun checkSelections() {
        selectedSpice = ArrayList<String>()

        //Iterate through topping list and pull selected toppings
        for(i in 0 until spiceListView.count) {
            if(spiceListView.isItemChecked(i)) {
                selectedSpice.add(spiceListView.getItemAtPosition(i).toString())
            } else {
                selectedSpice.remove(spiceListView.getItemAtPosition(i).toString())
            }
        }

        //Iterate through list and find selected size
        for(i in 0 until curryListView.count) {
            if(curryListView.isItemChecked(i)) {
                selectedCurry = curryListView.getItemAtPosition(i).toString()
                selectedSpice.add(curryListView.getItemAtPosition(i).toString())
            }
        }

        //Create Sub object from selections
        val selectedFood: Food?= Food("Curry", selectedSpice, selectedCurry)

        val intent = Intent(this, FinalizeOrderActivity::class.java)
        intent.putExtra(CurryOptionsActivity.FOOD, selectedFood)
        startActivity(intent)

        //for(i in selectedToppings) print(i + " ")
        //println("Size is $selectedSize")
    }

    //Add the Extras
    fun populateExtras() {
        spiceList?.add("Mild")
        spiceList?.add("Medium")
        spiceList?.add("Spicy")
    }

}