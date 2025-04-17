package com.example.foodieapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.example.foodieapp.viewModel.CategoryMealsViewModel
import com.example.foodieapp.adapters.CategoryMealsAdapter
import com.example.foodieapp.databinding.ActivityCategoryMealsBinding
import com.example.foodieapp.fragments.HomeFragment
import com.example.foodieapp.fragments.HomeFragment.Companion.MEAL_ID
import com.example.foodieapp.fragments.HomeFragment.Companion.MEAL_NAME
import com.example.foodieapp.fragments.HomeFragment.Companion.MEAL_THUMB
import com.example.foodieapp.pojo.Meal
import com.example.foodieapp.pojo.MealsByCategory

class CategoryMealsActivity : AppCompatActivity() {
    lateinit var binding:ActivityCategoryMealsBinding
    lateinit var categoryMealsViewModel:CategoryMealsViewModel
    lateinit var categoryMealsAdapter: CategoryMealsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityCategoryMealsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prepareRecyclerView()
        categoryMealsViewModel= ViewModelProviders.of(this)[CategoryMealsViewModel::class.java]
        categoryMealsViewModel.getMealsByCategory(intent.getStringExtra(HomeFragment.CATEGORY_NAME)!!)
        categoryMealsViewModel.observeMealsLiveData().observe(this, Observer { mealsList->
           binding.tvCategoryCount.text="Meals count : "+mealsList.size.toString()
            mealsList.forEach{
                Log.d("test",it.strMeal)
                Log.d("count", "RecyclerView item count: ${categoryMealsAdapter.itemCount}")
            }
            categoryMealsAdapter.setMealsList(mealsList , this)


        })

        }

    private fun prepareRecyclerView() {
        categoryMealsAdapter = CategoryMealsAdapter()
        binding.rvMeals.apply {
            layoutManager= GridLayoutManager(context,2,GridLayoutManager.VERTICAL,false)
            adapter=categoryMealsAdapter
        }
    }

    fun onMealClick(meal : MealsByCategory){
        val intent = Intent(this,MealActivity::class.java)
        intent.putExtra(MEAL_ID,meal.idMeal)
        intent.putExtra(MEAL_NAME,meal.strMeal)
        intent.putExtra(MEAL_THUMB,meal.strMealThumb)
        startActivity(intent)
    }
}
