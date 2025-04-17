package com.example.foodieapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodieapp.activities.CategoryMealsActivity
import com.example.foodieapp.databinding.MealItemsBinding
import com.example.foodieapp.pojo.MealsByCategory
class CategoryMealsAdapter:RecyclerView.Adapter<CategoryMealsAdapter.CategoryMealsViewModel>() {
    private var mealslist = ArrayList<MealsByCategory>()
    private lateinit var controller : CategoryMealsActivity
    fun setMealsList(mealsList:List<MealsByCategory> , controller : CategoryMealsActivity){
        this.mealslist.clear() // Clear existing items to avoid mixing data
        this.mealslist.addAll(mealsList) // Add all items from the new list
        this.controller = controller
        notifyDataSetChanged() // Notify the adapter that data has changed

    }
    inner class CategoryMealsViewModel(val binding:MealItemsBinding ):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryMealsViewModel {
        return CategoryMealsViewModel(
            MealItemsBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun getItemCount(): Int {
        return mealslist.size
    }

    override fun onBindViewHolder(holder: CategoryMealsViewModel, position: Int) {
        Glide.with(holder.itemView).load(mealslist[position].strMealThumb).into(holder.binding.imgMeal)
        holder.binding.tvMealName.text=mealslist[position].strMeal
        holder.itemView.setOnClickListener{
            controller.onMealClick(mealslist[position])
        }

    }
}
