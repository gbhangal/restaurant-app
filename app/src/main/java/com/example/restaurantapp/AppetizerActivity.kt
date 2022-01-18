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

class AppetizerActivity : AppCompatActivity() {

    var appetizerList: ArrayList<String> ?= ArrayList()
    lateinit var appetizerListView: ListView

    var extrasList: ArrayList<String> ?= ArrayList()
    lateinit var extrasListView: ListView

    lateinit var appetizerAdapter : ArrayAdapter<String>
    lateinit var extrasAdapter: ArrayAdapter<String>

    lateinit var orderButton: Button

    lateinit var selectedExtra: ArrayList<String>
    lateinit var selectedAppetizer: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appetizer)

        init()

        extrasAdapter = ArrayAdapter(applicationContext, android.R.layout.simple_list_item_multiple_choice, extrasList!!)
        extrasListView?.adapter = extrasAdapter
        extrasListView.choiceMode = ListView.CHOICE_MODE_MULTIPLE
        setCurry()

        orderButton.setOnClickListener() {
            checkSelections()
        }

    }

    companion object {
        const val FOOD = "selectedFood"
    }

    fun init() {
        appetizerListView = findViewById(R.id.curryList)
        extrasListView = findViewById(R.id.spiceList)
        orderButton = findViewById(R.id.curryOrderButton)
        populateExtras()
    }

    //Pull the meats from the database
    private fun setCurry() {
        //Pull the information from the .php script for categories
        //Prevents unauthorized use of the database
        val queue = Volley.newRequestQueue(this)
        val url = "http://192.168.1.51/restaurantQuery/getAppetizerTypes.php"

        val jsonRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                for(i in 0 until response.length()) {
                    val toppings = response.getJSONObject(i)
                    val toppingName = toppings.getString("OptionName")
                    //println(categoryFoodName)
                    val toppingCost = toppings.getDouble("OptionCost")

                    appetizerList?.add(toppingName)
                }

                appetizerAdapter = ArrayAdapter(applicationContext, android.R.layout.simple_list_item_single_choice, appetizerList!!)
                appetizerListView?.adapter = appetizerAdapter
                appetizerListView.choiceMode = ListView.CHOICE_MODE_SINGLE
                appetizerListView.itemsCanFocus = true

            },
            Response.ErrorListener {
                println("Error connecting to php script!")
            })

        queue.add(jsonRequest)
    }

    private fun checkSelections() {
        selectedExtra = ArrayList<String>()

        //Iterate through topping list and pull selected toppings
        for(i in 0 until extrasListView.count) {
            if(extrasListView.isItemChecked(i)) {
                selectedExtra.add(extrasListView.getItemAtPosition(i).toString())
            } else {
                selectedExtra.remove(extrasListView.getItemAtPosition(i).toString())
            }
        }

        //Iterate through list and find selected size
        for(i in 0 until appetizerListView.count) {
            if(appetizerListView.isItemChecked(i)) {
                selectedAppetizer = appetizerListView.getItemAtPosition(i).toString()
                selectedExtra.add(appetizerListView.getItemAtPosition(i).toString())
            }
        }

        //Create Sub object from selections
        val selectedFood: Food?= Food("Appetizer", selectedExtra, selectedAppetizer)

        val intent = Intent(this, FinalizeOrderActivity::class.java)
        intent.putExtra(CurryOptionsActivity.FOOD, selectedFood)
        startActivity(intent)

        //for(i in selectedToppings) print(i + " ")
        //println("Size is $selectedSize")
    }

    //Add the Extras
    fun populateExtras() {
        extrasList?.add("Mangochutney")
        extrasList?.add("Mix Spicy Pickle")
        extrasList?.add("Marinara Sauce")
    }

}