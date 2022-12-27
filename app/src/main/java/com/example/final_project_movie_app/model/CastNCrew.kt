package com.example.final_project_movie_app.model

import com.google.gson.annotations.SerializedName

data class CastNCrew (
    @SerializedName("profile_path") var profilePath : String? = null,
    @SerializedName("name") var name : String? = null,
)