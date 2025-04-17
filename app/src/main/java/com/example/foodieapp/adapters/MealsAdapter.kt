package com.example.foodieapp.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodieapp.databinding.MealItemsBinding
import com.example.foodieapp.pojo.Category
import com.example.foodieapp.pojo.Meal
import com.example.foodieapp.pojo.MealsByCategory

class MealsAdapter: RecyclerView.Adapter<MealsAdapter.FavoritesMealsAdapterViewHolder>() {

  inner  class FavoritesMealsAdapterViewHolder(val binding:MealItemsBinding):RecyclerView.ViewHolder(binding.root){
  }
    var onItemClick: ((Meal) -> Unit)? = null
  private val diffutill=object:DiffUtil.ItemCallback<Meal>(){
      override fun areItemsTheSame(oldItem: Meal, newItem: Meal): Boolean {
          return oldItem.idMeal==newItem.idMeal
      }

      override fun areContentsTheSame(oldItem: Meal, newItem: Meal): Boolean {
          return oldItem==newItem
      }


  }

    val differ = AsyncListDiffer(this,diffutill)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavoritesMealsAdapterViewHolder {
        return FavoritesMealsAdapterViewHolder(
        MealItemsBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        )
    }

    override fun getItemCount(): Int {
      return differ.currentList.size
    }

    override fun onBindViewHolder(holder: FavoritesMealsAdapterViewHolder, position: Int) {
        val meal = differ.currentList[position]
        Log.d("MealsAdapter", "Binding meal: ${meal.strMeal}")
        Glide.with(holder.itemView).load(meal.strMealThumb).into(holder.binding.imgMeal)
        holder.binding.tvMealName.text=meal.strMeal

        holder.itemView.setOnClickListener{
            onItemClick!!.invoke(meal)
        }
    }



}