package com.example.restaurantapp.food

import android.os.Parcel
import android.os.Parcelable

class Food() : Parcelable {

    var icons: Int ? = 0
    var name: String ? = null
    var toppings: ArrayList<String> ?= null
    var size: String ?= null

    constructor(parcel: Parcel) : this() {
        this.name = parcel.readString()
        this.toppings = parcel.createStringArrayList()
        this.size = parcel.readString()
    }

    constructor(icons: Int?, name: String?): this() {
        this.name = name
        this.icons = icons
    }

    constructor(name: String?, toppings: ArrayList<String>, size: String): this() {
        this.name = name
        this.toppings = toppings
        this.size = size
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        //parcel.writeValue(icons)
        parcel.writeString(name)
        parcel.writeStringList(toppings)
        parcel.writeString(size)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Food> {
        override fun createFromParcel(parcel: Parcel): Food {
            return Food(parcel)
        }

        override fun newArray(size: Int): Array<Food?> {
            return arrayOfNulls(size)
        }
    }
}