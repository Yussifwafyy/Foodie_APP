package com.example.foodieapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.foodieapp.pojo.Meal

@Database(entities =[Meal::class], version = 1)
@TypeConverters(MealTypeConverter::class)
abstract class MealDataBase:RoomDatabase() {
    abstract fun mealDao():MealDao
    companion object{
        var INSTANCE:MealDataBase? = null


        @Synchronized
        fun getInstance(context:Context):MealDataBase{
            if(INSTANCE==null){
                INSTANCE=Room.databaseBuilder(
                    context,
                    MealDataBase::class.java,
                    "meal.db"
                ).fallbackToDestructiveMigration()
                    .build()
            }
            return INSTANCE as MealDataBase
        }
    }
}