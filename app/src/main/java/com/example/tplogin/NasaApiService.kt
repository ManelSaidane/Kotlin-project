package com.example.tplogin

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NasaApiService {
    @GET("planetary/apod")
    fun getNasaData(@Query("api_key") apiKey: String): Call<NasaApiResponse>
}