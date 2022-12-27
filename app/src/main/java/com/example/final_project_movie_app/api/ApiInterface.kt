package com.example.final_project_movie_app.api

import com.example.final_project_movie_app.model.CastCrewList
import com.example.final_project_movie_app.model.MovieList
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterface {
    @GET("3/movie/{typeMovie}")
    fun getMovieList(@Path("typeMovie") typeListMovie : String, @Query("api_key") apiKey:String,@Query("page") pageNumber :String): retrofit2.Call<MovieList>

    @GET("3/movie/{movieID}/credits")
    fun getCastAndCrew(@Path("movieID") id : Int, @Query("api_key") apiKey:String): retrofit2.Call<CastCrewList>
}