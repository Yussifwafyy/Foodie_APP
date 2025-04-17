package com.example.foodieapp.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.foodieapp.R
import com.example.foodieapp.database.MealDataBase
import com.example.foodieapp.fragments.HomeFragment.Companion.MEAL_ID
import com.example.foodieapp.fragments.HomeFragment.Companion.MEAL_NAME
import com.example.foodieapp.fragments.HomeFragment.Companion.MEAL_THUMB
import com.example.foodieapp.pojo.Meal
import com.example.foodieapp.pojo.MealsByCategory
import com.example.foodieapp.viewModel.HomeViewModel
import com.example.foodieapp.viewModel.HomeViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    val viewModel:HomeViewModel by lazy {
        val mealDatabase= MealDataBase.getInstance(this)
        val homeViewModelProviderFactory = HomeViewModelFactory(mealDatabase)
        ViewModelProvider(this,homeViewModelProviderFactory)[HomeViewModel::class.java]
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       Thread.sleep(3000)
//        installSplashScreen()

        setContentView(R.layout.activity_main)
        val bottomNav=findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val navController=Navigation.findNavController(this, R.id.frag_host)
        NavigationUI.setupWithNavController(bottomNav,navController)


    }

    fun onMealClick(meal : Meal){
        val intent = Intent(this,MealActivity::class.java)
        intent.putExtra(MEAL_ID,meal.idMeal)
        intent.putExtra(MEAL_NAME,meal.strMeal)
        intent.putExtra(MEAL_THUMB,meal.strMealThumb)
        startActivity(intent)
    }

}