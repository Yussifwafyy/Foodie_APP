package com.example.foodieapp.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.foodieapp.R
import com.example.foodieapp.viewModel.HomeViewModel
import com.example.foodieapp.activities.CategoryMealsActivity
import com.example.foodieapp.activities.MainActivity
import com.example.foodieapp.activities.MealActivity
import com.example.foodieapp.adapters.CategoriesAdapter
import com.example.foodieapp.adapters.SugesstedAdapter
//import com.example.foodiez.ViewModel.HomeViewModel
import com.example.foodieapp.databinding.FragmentHomeBinding
import com.example.foodieapp.pojo.MealsByCategory
import com.example.foodieapp.pojo.Meal

class HomeFragment : Fragment() {
    private lateinit var binding:FragmentHomeBinding
    private lateinit var homeMvvm: HomeViewModel
    private lateinit var randomMeal: Meal
    private lateinit var SugesstedItemsAdapter:SugesstedAdapter
    private lateinit var categoriesAdapter:CategoriesAdapter

    companion object{
        const val MEAL_ID=" com.example.foodieapp.fragments.idMeal"
        const val MEAL_NAME=" com.example.foodieapp.fragments.nameMeal"
        const val MEAL_THUMB = " com.example.foodieapp.fragments.thumbMeal"
        const val CATEGORY_NAME = " com.example.foodieapp.fragments.categoryName"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inside a Fragment
       // homeMvvm = ViewModelProviders.of(this)[HomeViewModel::class.java]
        homeMvvm = (activity as MainActivity).viewModel

        SugesstedItemsAdapter = SugesstedAdapter()

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding=FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preparePopularItemsRecycler()

        homeMvvm.getRandomMeal()
        observerRandomMeal()
        onRandomMealCLick()

        homeMvvm.getSuggestedItems()
        observeSuggestedItemLiveData()
        onSuggestedClick()

        prepareCategoriesRecycler()
        homeMvvm.getCategories()
        observeCategoriesLiveData()
        onCategoryClick()

        onSearchIconCLick()


    }

    private fun onSearchIconCLick() {
        binding.imgSearch.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }
    }

    private fun onCategoryClick() {
        categoriesAdapter.onItemClick={category ->
            val intent=Intent(activity,CategoryMealsActivity::class.java)
            intent.putExtra(CATEGORY_NAME,category.strCategory)
            startActivity(intent)

        }
    }

    private fun prepareCategoriesRecycler() {
        categoriesAdapter = CategoriesAdapter()
        binding.recViewCategories.apply {
            layoutManager=GridLayoutManager(context,3,GridLayoutManager.VERTICAL,false)
            adapter=categoriesAdapter
        }
    }

    private fun observeCategoriesLiveData() {
        homeMvvm.observeCategoriesLiveData().observe(viewLifecycleOwner, Observer { categories->

               categoriesAdapter.setCategoriesList(categories)



        })
    }

    private fun onSuggestedClick() {
        SugesstedItemsAdapter.onItemClick={randomMeal->
            val intent=Intent(activity,MealActivity::class.java)
            intent.putExtra(MEAL_ID,randomMeal.idMeal)
            intent.putExtra(MEAL_NAME,randomMeal.strMeal)
            intent.putExtra(MEAL_THUMB,randomMeal.strMealThumb)
            startActivity(intent)

        }
    }

    private fun preparePopularItemsRecycler() {
        binding.recViewMealsPopular.apply {
            layoutManager=LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false)
            adapter=SugesstedItemsAdapter
        }
    }

    private fun observeSuggestedItemLiveData() {
        homeMvvm.observeSuggestedItemsLiveData().observe(viewLifecycleOwner,
         {mealList->
             SugesstedItemsAdapter.setMeals(mealsList = mealList as ArrayList<MealsByCategory>)

    })
}

    @SuppressLint("SuspiciousIndentation")
    private fun onRandomMealCLick(){
        binding.randomMeal.setOnClickListener{
            (activity as MainActivity).onMealClick(randomMeal)
        }
    }
    private fun observerRandomMeal(){
        homeMvvm.observeRandomMealLiveData().observe(viewLifecycleOwner
        ) { meal ->
            Glide.with(this@HomeFragment)
                .load(meal!!.strMealThumb)
                .into(binding.imgRandomMeal)
            this.randomMeal=meal
        }
    }


}
