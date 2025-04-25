package com.example.foodieapp.pojo

object MealData {
     private lateinit var meal: Meal

    fun getVideoURL():String{
        return meal.strYoutube.toString()
    }

}
