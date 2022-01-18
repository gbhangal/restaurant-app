package com.example.restaurantapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.telephony.PhoneNumberUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.restaurantapp.food.Food
import com.example.restaurantapp.roomDB.OrderedFood
import com.example.restaurantapp.roomDB.OrderedFoodDatabase
import kotlinx.android.synthetic.main.activity_finalize_order.*
import java.time.LocalDateTime
import kotlin.concurrent.thread


class FinalizeOrderActivity : AppCompatActivity() {

    lateinit var phoneNumber: String
    lateinit var name: String

    lateinit var food: Food

    lateinit var totalPriceSel: TextView
    lateinit var nameInput: EditText
    lateinit var phoneInput: EditText
    lateinit var submitOrder: Button
    lateinit var clearOrder: Button

    var queue: RequestQueue? = null

    var orderID: String ?= null
    var foodID: Int ?= null


    //Find the values from the previous activity
    @RequiresApi(Build.VERSION_CODES.O)
    fun init() {
        nameInput = findViewById(R.id.nameInput)
        phoneInput = findViewById(R.id.phoneInput)
        submitOrder = findViewById(R.id.orderButton)
        clearOrder = findViewById(R.id.clearOrder)

        phoneInput.addTextChangedListener(PhoneNumberFormattingTextWatcher())

        queue = Volley.newRequestQueue(this)

        submitOrder.setOnClickListener() {
            if(validInput()) {
                //Add customer information to database
                //Starts chain to add orders to database as well
                addCustomer()
            }
        }

        orderMore.setOnClickListener() {
            //Go back to order menu
            val intent = Intent( this, OrderActivity::class.java)
            startActivity(intent)

        }

        //Clear the order and price
        clearOrder.setOnClickListener() {
            val db = Room.databaseBuilder (
                applicationContext,
                OrderedFoodDatabase::class.java, "database-name"
            )   .fallbackToDestructiveMigration()
                .build()

            thread() {
                db.clearAllTables()
            }

            calcPrice()
        }

    }

    private fun calcPrice() {
        val db = Room.databaseBuilder(
            applicationContext,
            OrderedFoodDatabase::class.java, "database-name"
        ).fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()


        var totalPrice: Double = 0.0

        val foodDao = db.OrderedFoodDao()

        val foodInOrder = foodDao.readFood()

        foodInOrder.observe(this, Observer {
            foodInOrder.value!!.forEach {
                Log.d("foodName is: ", it.foodName)
                if (it.foodName.equals("Pizza")) {
                    var pizzaPrice: Double = 0.0

                    if (it.toppings.contains("Small")) {
                        if (it.toppings.size == (5 + 1)) {
                            pizzaPrice = 11.0
                        } else if (it.toppings.size > (5 + 1)) {
                            pizzaPrice = 11 + ((it.toppings.size - 1 - 5) * .75)
                        } else if (it.toppings.size <= (4 + 1)) {
                            pizzaPrice = 8 + ((it.toppings.size - 1) * .75)
                        }
                    } else if (it.toppings.contains("Medium")) {
                        if (it.toppings.size == (5 + 1)) {
                            pizzaPrice = 15.0
                        } else if (it.toppings.size > (5 + 1)) {
                            pizzaPrice = 15 + ((it.toppings.size - 1 - 5) * 1.5)
                        } else if (it.toppings.size <= (3 + 1)) {
                            pizzaPrice = 10 + ((it.toppings.size - 1) * 1.5)
                        }
                    } else if (it.toppings.contains("Large")) {
                        if (it.toppings.size == (5 + 1)) {
                            pizzaPrice = 16.75
                        } else if (it.toppings.size > (5 + 1)) {
                            pizzaPrice = 16.75 + ((it.toppings.size - 1 - 5) * 1.5)
                        } else if (it.toppings.size <= (3 + 1)) {
                            pizzaPrice = 12 + ((it.toppings.size - 1) * 1.5)
                        }
                    }

                    totalPrice += pizzaPrice
                }

                if (it.foodName.equals("ColdSub")) {
                    var subPrice: Double = 0.0

                    if (it.toppings.contains("Pickle")) {
                        subPrice += .25
                    }

                    if (it.toppings.contains("Ham") && it.toppings.contains("Turkey") && it.toppings.contains("Salami") && it.toppings.contains("Roast Beef")) {
                        subPrice += 7.75
                        if(it.toppings.contains("Cheese")) {
                            subPrice += .25
                        }
                    } else if(it.toppings.contains("Ham") && it.toppings.contains("Turkey") && it.toppings.contains("Roast Beef")) {
                        subPrice += 6.5
                        if(it.toppings.contains("Cheese")) {
                            subPrice += .25
                        }
                    } else if(it.toppings.contains("Gyro")) {
                        subPrice += 6
                        if(it.toppings.contains("Cheese")) {
                            subPrice += .5
                        }
                    } else if(it.toppings.contains("Roast Beef")) {
                        subPrice += 5.75
                        if(it.toppings.contains("Cheese")) {
                            subPrice += .25
                        }
                    } else if(it.toppings.contains("Turkey")) {
                        subPrice += 5.5
                        if(it.toppings.contains("Cheese")) {
                            subPrice += .25
                        }
                    } else if(it.toppings.contains("Ham") && it.toppings.contains("Salami")) {
                        subPrice += 5.25
                        if(it.toppings.contains("Cheese")) {
                            subPrice += .25
                        }
                    } else if(it.toppings.contains("Ham")) {
                        subPrice += 4.5
                        if(it.toppings.contains("Cheese")) {
                            subPrice += .25
                        }
                    } else if(it.toppings.contains("Pepperoni")) {
                        subPrice += 4.5
                        if(it.toppings.contains("Cheese")) {
                            subPrice += .25
                        }
                    } else if(it.toppings.contains("Salami")) {
                        subPrice += 4.5
                        if(it.toppings.contains("Cheese")) {
                            subPrice += .25
                        }
                    } else if(it.toppings.contains("Cheese")) {
                        subPrice += 4.5
                    }
                    totalPrice += subPrice
                }

                if(it.foodName.equals("HotSub")) {
                    var subPrice: Double = 5.0

                    if (it.toppings.contains("Pepper") && it.toppings.contains("Mushroom") && it.toppings.contains("Grilled Onion")) {
                        subPrice+= 1.0
                    } else {
                        if (it.toppings.contains("Pepper")) {
                            subPrice+= .5
                        }
                        if (it.toppings.contains("Mushroom")) {
                            subPrice+= .5
                        }
                        if (it.toppings.contains("Grilled Onion")) {
                            subPrice+= .5
                        }
                    }

                    if(it.toppings.contains("Sauce")) {
                        subPrice+= .5
                    }

                    totalPrice+= subPrice
                }

                if(it.foodName.equals("Curry")) {
                    var curryPrice: Double = 0.0

                    if(it.toppings.contains("Chicken") || it.toppings.contains("Beef") || it.toppings.contains("Mutter Paneer")|| it.toppings.contains("Palak Paneer")) {
                        curryPrice = 11.25
                    } else if(it.toppings.contains("Lamb")) {
                        curryPrice = 12.25
                    } else if(it.toppings.contains("Mixed Vegetables")) {
                        curryPrice = 10.25
                    }

                    totalPrice += curryPrice
                }

                if(it.foodName.equals("Dinner")) {
                        totalPrice += 8.50
                }

                if(it.foodName.equals("Appetizer")) {
                    var appetPrice: Double = 0.0

                    if(it.toppings.contains("Garlic Bread")) {
                        appetPrice+= 2.75
                    } else if(it.toppings.contains("Garlic Bread with Cheese")) {
                        appetPrice+= 3.75
                    } else if(it.toppings.contains("Mozarella Cheese Sticks")) {
                        appetPrice+= 4.25
                    } else if(it.toppings.contains("Naan Bread")) {
                        appetPrice+= 1.75
                    } else if(it.toppings.contains("Vegetable Samosas")) {
                        appetPrice+= 2.75
                    }

                    if(it.toppings.contains("Mangochutney")) {
                        appetPrice+= 1
                    }

                    if(it.toppings.contains("Mix Spicy Pickle")) {
                        appetPrice+= 1
                    }

                    if(it.toppings.contains("Marinara Sauce")) {
                        appetPrice+= .5
                    }

                }

                if(it.foodName.equals("Salad")) {
                    var saladPrice: Double = 0.0

                    if(it.toppings.contains("Small")) {
                        if(it.toppings.contains("Tossed")) {
                            saladPrice+= 3
                        } else if(it.toppings.contains("Chef")) {
                            saladPrice+= 4.5
                        } else if(it.toppings.contains("Antipasto")) {
                            saladPrice+= 4.75
                        }
                    } else if(it.toppings.contains("Large")) {
                        if(it.toppings.contains("Tossed")) {
                            saladPrice+= 5
                        } else if(it.toppings.contains("Chef")) {
                            saladPrice+= 6.5
                        } else if(it.toppings.contains("Antipasto")) {
                            saladPrice+= 6.75
                        }
                    }

                    totalPrice+= saladPrice
                }

            }

            totalPriceSel.setText("$" + totalPrice.toString())
            Log.d("totalPrice of Order", totalPrice.toString())
            totalPrice = 0.0

        })

        //Log.d("OrderPrice", totalPrice.toString())
    }

    //Builds URL for customer addition
    private fun buildCustURL(): String {
        var baseURL = "http://192.168.1.51/restaurantQuery/insertCustomer.php"

        val name = nameInput.text.toString()
        val phoneNumber = phoneInput.text.toString()

        val preparedURL = "$baseURL?name=$name&phoneNumber=$phoneNumber"

        return preparedURL
    }

    //Add customer to database, if not already existing
    @RequiresApi(Build.VERSION_CODES.O)
    private fun addCustomer() {
        //Build PHP URL to add customer
        val url = buildCustURL()

        //Request a string response from the provided URL.
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            Response.Listener<String> { response ->
                addOrder()
            },
            Response.ErrorListener { val toast = Toast.makeText(applicationContext, "Failed to add Customer!", Toast.LENGTH_SHORT) })

        // Add the request to the RequestQueue.
        queue?.add(stringRequest)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun addOrder() {

        val url = buildOrderURL()
        //print(url)

        // Request a string response from the provided URL.
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            Response.Listener<String> { response ->
                val lastOrderID = response.toString()
                orderID = lastOrderID
                setFoods()
                Log.d("print", "Order ID: " + lastOrderID)
            },
            Response.ErrorListener { val toast = Toast.makeText(applicationContext, "Failed to add Order!", Toast.LENGTH_SHORT) })

        // Add the request to the RequestQueue.
        queue?.add(stringRequest)

    }

    //Function concatenates information
    @RequiresApi(Build.VERSION_CODES.O)
    private fun buildOrderURL(): String {
        var baseURL = "http://192.168.1.51/restaurantQuery/insertOrder.php"

        val date = LocalDateTime.now().toLocalDate().toString()
        //Log.d("print", date);
        val orderComplete = 0
        val quantity = 1
        val phoneNumber = phoneInput.text.toString()

        //Log.d("print", "$baseURL?orderDate=$date&orderComplete=$orderComplete&quantity=$quantity&phoneNumber=$phoneNumber")

        return "$baseURL?orderDate=$date&orderComplete=$orderComplete&quantity=$quantity&phoneNumber=$phoneNumber"
    }

    //Basic URL to show values
    private fun buildFoodIdUrl(food: String): String {
        var baseURL = "http://192.168.1.51/restaurantQuery/getFoodID.php"
        return "$baseURL?foodName=${food}"
    }

    //Concatenate all the values to the URL to add an order
    private fun buildToppingURL(topping: String, numFood: Int, foodType: String) : String {
        var baseURL = "http://192.168.1.51/restaurantQuery/insertToppings.php"

        val totalPrice: Double = 0.0
        val foodName = foodType
        val optionName = topping
        val numOfFood = numFood

        return "$baseURL?totalPrice=$totalPrice&orderID=$orderID&foodName=$foodName&optionName=$optionName&numOfFood=$numOfFood"
    }



    //Add the food to the order
    private fun setFoods() {

        val db = Room.databaseBuilder (
            applicationContext,
            OrderedFoodDatabase::class.java, "database-name"
        )   .fallbackToDestructiveMigration()
            .build()

        val foodDao = db.OrderedFoodDao()

        val foodInOrder= foodDao.readFood()

        var numOfFood: Int = 0

        foodInOrder.observe(this, Observer {
            //Log.d("print", foodInOrder.value.toString())

            foodInOrder.value!!.forEach {
                //Log.d("print", it.toString())
                //Log.d("print", it.toppings.toString())

                var foodType: String = it.foodName

                //val url = buildFoodIdUrl(it.foodName)
                //print(url)

                numOfFood++

                //Log.d("print", "Food ID: " + foodID.toString())

                Log.d("Start of toppings", "Start of toppings")

                for(topping in it.toppings) {
                    Log.d("print", topping)
                    addToppings(topping, numOfFood, foodType)
                }

            }

        })
    }

    //Add toppings to the database
    private fun addToppings(topping: String, numFood: Int, foodName: String) {

        var foodType: String = foodName

        val url = buildToppingURL(topping, numFood, foodType)
        //Log.d("print", url)

        // Request a string response from the provided URL.
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            Response.Listener<String> { response ->

            },
            Response.ErrorListener { val toast = Toast.makeText(applicationContext, "Failed to add topping", Toast.LENGTH_SHORT) })

        // Add the request to the RequestQueue.
        queue?.add(stringRequest)
    }

    private fun addFoodToDB() {
        val db = Room.databaseBuilder (
            applicationContext,
            OrderedFoodDatabase::class.java, "database-name"
        )   .fallbackToDestructiveMigration()
            .build()

        val foodDao = db.OrderedFoodDao()

        val orderedFood = OrderedFood(0, food.name!!, food.toppings!!)

        Log.d("print", "hello")

        thread() {
            foodDao.addFood(orderedFood)
        }

        calcPrice()

        db.close()
    }

    //Pull the inputs and store it in a food object
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finalize_order)

        intent?.let {
            food = intent.extras!!.getParcelable<Food>(PizzaOptionsActivity.FOOD) as Food

            totalPriceSel = findViewById(R.id.totalPrice) as TextView

            addFoodToDB()

            //println(food.name.toString())
            //val myText = findViewById(R.id.textView) as TextView
            //myText.text = food.toppings.toString()
        }

        init()
    }

    //check for valid input
    private fun validInput() : Boolean {
        var isValid: Boolean = true
        if(nameInput.text.toString() == "") {
            val toast = Toast.makeText(applicationContext, "Invalid name", Toast.LENGTH_SHORT)
            toast.show()
            isValid = false
        }
        phoneNumber = PhoneNumberUtils.formatNumber(phoneInput.text.toString())
        if(PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber)) {
            val toast:Toast = Toast.makeText(applicationContext, "Invalid phone number", Toast.LENGTH_SHORT)
            toast.show()
            isValid = false
        }
        return isValid
    }
}