package com.example.foodieapp.database
import androidx.lifecycle.LiveData
import androidx.room.Dao
//import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.*
import com.example.foodieapp.pojo.Meal

@Dao
interface MealDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(meal:Meal)

    @Delete
    suspend fun delete(meal: Meal)

    @Query("SELECT * FROM mealsInformation")
    fun getAllMeals():LiveData<List<Meal>>

}