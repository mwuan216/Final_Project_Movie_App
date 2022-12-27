package com.example.final_project_movie_app.api

import com.example.final_project_movie_app.constant.APIconstant
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

open class RetrofitClient {
    private lateinit var retrofit: Retrofit

    fun getRetrofitInstance() : Retrofit{
        retrofit = Retrofit.Builder()
            .baseUrl(APIconstant.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit
    }
}