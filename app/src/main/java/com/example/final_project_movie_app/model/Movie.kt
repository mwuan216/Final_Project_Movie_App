package com.example.final_project_movie_app.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Movie (
    @SerializedName("id") var id : Int,
    @SerializedName("title") var title : String,
    @SerializedName("overview") var overview : String,
    @SerializedName("vote_average") var voteAverage : Double,
    @SerializedName("release_date")  var releaseDate : String,
    @SerializedName("poster_path") var posterPath : String,
    @SerializedName("adult") var adult : Boolean,
    var isFavorite : Boolean = false,
    var reminderTimeDisplay : String ="",
    var reminderTime : String = ""
) : Serializable