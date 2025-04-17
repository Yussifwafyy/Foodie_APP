package com.example.foodieapp.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.example.foodieapp.R
import com.example.foodieapp.database.MealDataBase
import com.example.foodieapp.viewModel.MealViewModel
import com.example.foodieapp.databinding.ActivityMealBinding
import com.example.foodieapp.fragments.HomeFragment
import com.example.foodieapp.pojo.Meal
import com.example.foodieapp.viewModel.MealViewModelFactory


class MealActivity : AppCompatActivity() {
    private lateinit var mealId:String
    private lateinit var mealName:String
    private lateinit var mealThumb:String
    private lateinit var binding:ActivityMealBinding
    private lateinit var mealMvvm:MealViewModel
    private lateinit var youtubeLink:String
    private lateinit var myMeal: Meal
    private lateinit var webView: WebView
    //private lateinit var mealVideo:WebView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMealBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val mealDataBase= MealDataBase.getInstance(this)
        val viewModelFactory = MealViewModelFactory(mealDataBase)

       mealMvvm = ViewModelProvider(this, viewModelFactory)[MealViewModel::class.java]
        getMealInformationFromIntent()
        loadingCase()

        setInformationViews()
        mealMvvm.getMealDetail(mealId)
        observerMealDetailsLiveData()

        onYoutubeImageCLick()


        onFavClick()
            //val rawVideoUrl =myMeal.getVideoURL()



        }

    private fun onFavClick() {
        binding.btnSave.setOnClickListener {
            mealToSave?.let {
                mealMvvm.insertMeal(it)
                Toast.makeText(this,"Meal Saved",Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun setupWebView(meal: Meal) {
        val webView = findViewById<WebView>(R.id.img_youtube)

        // Check if the YouTube URL is valid
        val rawVideoURL = meal.strYoutube
        if (rawVideoURL.isNullOrEmpty()) {
            Log.e("awgawgawgawgawg", "YouTube URL is empty or null")
            return
        }

        // Extract the video ID from the URL
        val videoId = rawVideoURL.substringAfter("v=").substringBefore("&")

        // Check if video ID is found
        if (videoId.isNullOrEmpty()) {
            Log.e("awgawgawgawgawg", "Invalid YouTube URL format")
            return
        }

        // Create the embed URL
        val embedUrl = "https://www.youtube.com/embed/$videoId"
        val videoHtml = """
        <html>
            <body style="margin:0;padding:0;">
                <iframe width="100%" height="100%" src="$embedUrl" 
                        frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" 
                        allowfullscreen>
                </iframe>
            </body>
        </html>
        """

        // Enable JavaScript and load the video
        webView.settings.javaScriptEnabled = true
        webView.settings.loadWithOverviewMode = true
        webView.settings.useWideViewPort = true
        webView.webChromeClient = WebChromeClient()

        // Load the video HTML into the WebView
        webView.loadDataWithBaseURL(null, videoHtml, "text/html", "utf-8", null)
    }

    private fun getYoutubeCode(youtube : String?) : String{
        if (youtube.isNullOrBlank())
            return ""
        var i =0
        while(youtube[i] != '=')
            i++

        return youtube.substring(i+1)
    }
    private fun onYoutubeImageCLick(){
    binding.imgYoutube.setOnClickListener{
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(youtubeLink))
        startActivity(intent)
    }
    }

    private var mealToSave:Meal?=null
    private fun observerMealDetailsLiveData() {
        mealMvvm.observerMealDetailsLiveData().observe(this, object : Observer<Meal> {
            override fun onChanged(t: Meal) {
                onResponseCase()
                val meal = t
                mealToSave=meal
                //setupWebView(meal)
                binding.tvCategoryInfo.text= "Category : ${meal!!.strCategory}"
                binding.tvAreaInfo.text="Area : ${meal.strArea}"
                binding.tvInstructions.text="Instructions: ${meal.strInstructions}"
                youtubeLink= meal.strYoutube.toString()
                setupWebView(meal)
            }
        })
    }

      private fun  setInformationViews(){
            Glide.with(applicationContext)
                .load(mealThumb)
                .into(binding.imgMealDetail)

          binding.collapsingToolbar.title=mealName
          binding.collapsingToolbar.setCollapsedTitleTextColor(resources.getColor(R.color.white))
          binding.collapsingToolbar.setExpandedTitleColor(resources.getColor(R.color.white))
        }


    private fun getMealInformationFromIntent(){
        val intent=intent
        mealId=intent.getStringExtra(HomeFragment.MEAL_ID)!!
        mealName=intent.getStringExtra(HomeFragment.MEAL_NAME)!!
        mealThumb=intent.getStringExtra(HomeFragment.MEAL_THUMB)!!

    }
    private fun loadingCase(){
        binding.progressBar.visibility=View.VISIBLE
    binding.btnSave.visibility=View.INVISIBLE
        binding.tvInstructions.visibility=View.INVISIBLE
        binding.tvCategoryInfo.visibility=View.INVISIBLE
        binding.tvAreaInfo.visibility=View.INVISIBLE
        binding.imgYoutube.visibility=View.INVISIBLE
    }
    private fun onResponseCase(){
        binding.progressBar.visibility=View.INVISIBLE
        binding.btnSave.visibility=View.VISIBLE
        binding.tvInstructions.visibility=View.VISIBLE
        binding.tvCategoryInfo.visibility=View.VISIBLE
        binding.tvAreaInfo.visibility=View.VISIBLE
        binding.imgYoutube.visibility=View.VISIBLE

    }

}