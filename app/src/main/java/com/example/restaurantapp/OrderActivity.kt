package com.example.restaurantapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.GridView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.restaurantapp.adapters.FoodAdapter
import com.example.restaurantapp.food.Food
import com.example.restaurantapp.roomDB.OrderedFoodDatabase
import org.json.JSONArray
import kotlin.concurrent.thread


public interface VolleyCallBack{
    fun onValueChange(response: JSONArray)
}

class OrderActivity : AppCompatActivity(), AdapterView.OnItemClickListener {

    var arrayList:ArrayList<Food> ?= ArrayList()
    var gridView: GridView?= null
    var foodAdapter: FoodAdapter?= null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        //Grab the gridView
        gridView = findViewById(R.id.foodGridView)

        //arrayList = ArrayList()

        setFoodList()

        for(element in arrayList!!) println(element.toString())
         //Grab the lists of food

        //getFoodCategory()

    }

    fun onValueChange(response: JSONArray){
        setFoodList(response)
    }

    //Populate the interface with values
    private fun setFoodList() {
        //Pull the information from the .php script for categories
        //Prevents unauthorized use of the database
        val queue = Volley.newRequestQueue(this)
        val url = "http://192.168.1.51/restaurantQuery/foodCategory.php"

        val jsonRequest = JsonArrayRequest(Request.Method.GET, url, null,
            Response.Listener { response ->
                //println("$response")
                //setFoodList(response)
                onValueChange(response)
            },
            Response.ErrorListener {
                println("Error connecting to php script!")
            })

        queue.add(jsonRequest)
    }


    //Will be using database to pull food
    private fun setFoodList(queryFoodList: JSONArray) {
        //Parse the SQL query and grab the category of the food
        for(i in 0 until queryFoodList.length()) {
            val foodName = queryFoodList.getJSONObject(i)
            val categoryFoodName = foodName.getString("FoodName")
            //println(categoryFoodName)

            //Push the information to the card
            val imageSrc = getResources().getIdentifier(categoryFoodName.toLowerCase(), "drawable", getPackageName())
            arrayList!!.add(Food(imageSrc, categoryFoodName))
        }

        foodAdapter = FoodAdapter(applicationContext, arrayList!!) //Populate the adapter with different foods
        gridView?.adapter = foodAdapter //Set the gridView to the list of food


        gridView?.onItemClickListener = this //Find what category is chosen

    }



    //Listen for press on category
    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        var items:Food = arrayList!!.get(p2)


        //Grab right option menu based on food selected
        when(items.name) {
            "Pizza" ->  {
                val intent = Intent(this, PizzaOptionsActivity::class.java)
                startActivity(intent)
            }

            "Stromboli" -> {
                val intent = Intent( this, SandwichOptionsActivity::class.java)
                startActivity(intent)
            }

            "ColdSub" -> {
                val intent = Intent( this, SandwichOptionsActivity::class.java)
                startActivity(intent)
            }

            "HotSub" -> {
                val intent = Intent( this, HotSandwichOptionsActivity::class.java)
                startActivity(intent)
            }

            "Curry" -> {
                val intent = Intent( this, CurryOptionsActivity::class.java)
                startActivity(intent)
            }

            "Dinner" -> {
                val intent = Intent(this, DinnerActivity::class.java)
                startActivity(intent)
            }

            "Appetizer" -> {
                val intent = Intent(this, AppetizerActivity::class.java)
                startActivity(intent)
            }

            "Salad" -> {
                val intent = Intent(this, SaladActivity::class.java)
                startActivity(intent)
            }

            else -> {print("Error: Couldn't change to new activity!")}

        }



    }


}