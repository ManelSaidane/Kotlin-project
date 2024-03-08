package com.example.tplogin

import android.media.Image
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.tplogin.R
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserListFragment : Fragment() {

    private lateinit var nasaApiService: NasaApiService
    private lateinit var imageView : ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user_list, container, false)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.nasa.gov/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        nasaApiService = retrofit.create(NasaApiService::class.java)

        // Make API request
        val apiKey = "xyxE3r9BUMcoBfOLMF3lOx94ZoH8T5Q0x9CDXCXN"
        val call = nasaApiService.getNasaData(apiKey)

        call.enqueue(object : Callback<NasaApiResponse> {
            override fun onResponse(call: Call<NasaApiResponse>, response: Response<NasaApiResponse>) {
                if (response.isSuccessful) {
                    val nasaData = response.body()
                    updateUi(nasaData)
                } else {
                    println("Error: ${response.code()} - ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<NasaApiResponse>, t: Throwable) {
                println("Network request failed: ${t.message}")
            }
        })
    }

    private fun updateUi(nasaData: NasaApiResponse?) {
        nasaData?.let {
            view?.findViewById<TextView>(R.id.titleTextView)?.text = it.title
            view?.findViewById<TextView>(R.id.explanationTextView)?.text = it.explanation
            view?.findViewById<TextView>(R.id.urlTextView)?.text = it.url
            view?.findViewById<TextView>(R.id.dateTextView)?.text = it.date
            view?.findViewById<TextView>(R.id.mediaTypeTextView)?.text = it.mediaType
            Glide.with(requireContext())
                .load(it.hdurl) // Assuming hdurl is the URL property in your data class
                .into(view?.findViewById(R.id.imageView)!!)
        }
        }
    }

