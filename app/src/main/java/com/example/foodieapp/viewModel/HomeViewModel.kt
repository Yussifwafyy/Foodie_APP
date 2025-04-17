package com.example.foodieapp.viewModel
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodieapp.database.MealDataBase
import com.example.foodieapp.pojo.Category
import com.example.foodieapp.pojo.CategoryList
import com.example.foodieapp.pojo.MealsByCategoryList
import com.example.foodieapp.pojo.MealsByCategory
import com.example.foodieapp.pojo.Meal
import com.example.foodieapp.pojo.MealList
import com.example.foodieapp.retrofit.RetrofitInstance
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Query

class HomeViewModel(
    private val mealDataBase: MealDataBase
):ViewModel() {
    private var randomMealLiveData = MutableLiveData<Meal>()
    private var SuggestedItemsLiveData=MutableLiveData<List<MealsByCategory>>()
    private var categoriesLiveData=MutableLiveData<List<Category>>()
    private var favoritesMealLiveData = mealDataBase.mealDao().getAllMeals()
    private val searchMealsLiveData = MutableLiveData<List<Meal>>()





    fun getRandomMeal(){
        RetrofitInstance.api.getRandomMeal().enqueue(object: Callback<MealList> {
            override fun onResponse(call: Call<MealList>, response: Response<MealList>) {
                if (response.body()!=null){
                    val randomMeal: Meal =response.body()!!.meals[0]
                    randomMealLiveData.value=randomMeal
                }else{
                    return
                }

            }
            override fun onFailure(call: Call<MealList>, t:Throwable){
                Log.d("Homefrag",t.message.toString())
            }



        })

    }
    fun getSuggestedItems(){
        RetrofitInstance.api.getSuggested("SeaFood").enqueue(object:Callback<MealsByCategoryList>{
            override fun onResponse(call: Call<MealsByCategoryList>, response: Response<MealsByCategoryList>) {
                if(response.body()!=null){
                    SuggestedItemsLiveData.value=response.body()!!.meals
                }
            }

            override fun onFailure(call: Call<MealsByCategoryList>, t: Throwable) {
                Log.d("HomeFragg",t.message.toString())
            }

        })
    }
    fun getCategories(){
        RetrofitInstance.api.getCategories().enqueue(object :Callback<CategoryList>{
            override fun onResponse(call: Call<CategoryList>, response: Response<CategoryList>) {
                response.body()?.let{ categoryList ->
                    categoriesLiveData.postValue(categoryList.categories)

                }
            }

            override fun onFailure(call: Call<CategoryList>, t: Throwable) {
                Log.e("HomeViewModel",t.message.toString())
            }

        })
    }
    fun deleteMeal(meal:Meal){
        viewModelScope.launch {
            mealDataBase.mealDao().delete(meal)
        }
    }
    fun insertMeal(meal: Meal){
        viewModelScope.launch {
            mealDataBase.mealDao().update(meal)
        }
    }
    fun searchMeals(searchQuery: String) = RetrofitInstance.api.searchMeals(searchQuery).enqueue(
        object :Callback<MealList>{
            override fun onResponse(call: Call<MealList>, response: Response<MealList>) {
                val mealsList = response.body()?.meals
                mealsList?.let {
                    searchMealsLiveData.postValue(it)
                }
            }

            override fun onFailure(call: Call<MealList>, t: Throwable) {
                Log.e("HomeViewModel",t.message.toString())
            }

        }
    )
    fun observeSearchedMealsLiveData():LiveData<List<Meal>>{
        return searchMealsLiveData
    }

    fun observeRandomMealLiveData():LiveData<Meal>{
        return randomMealLiveData
    }
    fun observeSuggestedItemsLiveData():LiveData<List<MealsByCategory>>{
        return SuggestedItemsLiveData
    }
    fun observeCategoriesLiveData(): LiveData<List<Category>> {
        return categoriesLiveData
    }
    fun observeFavoritesMealsLiveData():LiveData<List<Meal>>{
        return favoritesMealLiveData
    }
}