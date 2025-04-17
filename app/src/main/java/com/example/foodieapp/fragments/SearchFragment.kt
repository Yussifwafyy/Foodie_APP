package com.example.foodieapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.foodieapp.activities.MainActivity
import com.example.foodieapp.adapters.MealsAdapter
import com.example.foodieapp.databinding.FragmentSearchBinding
import com.example.foodieapp.viewModel.HomeViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SearchFragment : Fragment() {
    private lateinit var binding:FragmentSearchBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var searchRecyclerViewAdapter:MealsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       viewModel = (activity as MainActivity).viewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prepareRecyclerView()

        binding.imageSearchArrow.setOnClickListener{searchMeals()}

        observeSearchedMealsLiveData()

        var searchJob:Job?  = null
        binding.searchBar.addTextChangedListener { searchQuery->
            searchJob?.cancel()
            searchJob=lifecycleScope.launch {
                delay(100)
                viewModel.searchMeals(searchQuery.toString())

            }
        }

        searchRecyclerViewAdapter.onItemClick = { meal ->
            (activity as MainActivity).onMealClick(meal)
        }

    }

    private fun observeSearchedMealsLiveData() {
        viewModel.observeSearchedMealsLiveData().observe(viewLifecycleOwner, Observer { mealsList ->
            if (mealsList.isNullOrEmpty()) {
                Log.d("SearchFragment", "No meals found")
            } else {
                Log.d("SearchFragment", "Meals found: ${mealsList.size}")
                searchRecyclerViewAdapter.differ.submitList(mealsList)
            }
        })
    }


    private fun searchMeals() {
        val searchQuery = binding.searchBar.text.toString()
        if (searchQuery.isNotEmpty()){
            Log.d("SearchFragment", "Searching meals with query: $searchQuery")
            viewModel.searchMeals(searchQuery)
        }
    }


    private fun prepareRecyclerView() {
        searchRecyclerViewAdapter=MealsAdapter()
        binding.rvSearchedMeals.apply {
            layoutManager = GridLayoutManager(context,2,GridLayoutManager.VERTICAL,false)
            adapter = searchRecyclerViewAdapter
        }

    }

}
