package com.example.foodieapp.retrofit

import com.example.foodieapp.pojo.CategoryList
import com.example.foodieapp.pojo.MealsByCategoryList
import com.example.foodieapp.pojo.MealList
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MealAPI {
    @GET("random.php")
    fun getRandomMeal(): Call<MealList>
    @GET("Lookup.php")
    fun getMealDetails(@Query("i")id:String) : Call<MealList>
    @GET("filter.php?")
    fun getSuggested(@Query("c")categoryName:String): Call<MealsByCategoryList>
    @GET("categories.php")
    fun getCategories():Call<CategoryList>
    @GET("filter.php")
    fun getMealsByCategory(@Query("c") categoryName: String):Call<MealsByCategoryList>
    @GET("search.php")
    fun searchMeals(@Query("s")searchQuery: String) : Call<MealList>
}