package com.example.tplogin

import retrofit2.Call
import com.example.tplogin.data.MyDataItem
import retrofit2.Callback
import retrofit2.http.GET
interface ApiInterface {


    @GET("posts")
    fun getData(): Call<List<MyDataItem>>


}
