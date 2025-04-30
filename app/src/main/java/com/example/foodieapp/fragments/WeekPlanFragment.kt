package com.example.foodieapp.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.foodieapp.R
class WeeklyPlanFragment : Fragment() {

    private lateinit var weekPlanTextView: TextView
    private lateinit var shoppingListTextView: TextView
    private lateinit var backButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_week_plan, container, false)

        weekPlanTextView = view.findViewById(R.id.tv_week_plan)
        //shoppingListTextView = view.findViewById(R.id.tv_shopping_list)
        backButton = view.findViewById(R.id.btn_back)

        backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        return view
    }

    override fun onResume() {
        super.onResume()

        val prefs = requireContext().getSharedPreferences("meal_plan", Context.MODE_PRIVATE)
        val weeklyPlan = prefs.getString("weekly_plan", "")

        if (!weeklyPlan.isNullOrEmpty()) {
            weekPlanTextView.text = weeklyPlan
        } else {
            weekPlanTextView.text = "لا توجد خطة أسبوعية."
        }
    }
}
