package com.example.final_project_movie_app.model

import com.google.gson.annotations.SerializedName

data class CastCrewList (
    @SerializedName("id") var id : Int? = null,
    @SerializedName("cast") var castList : List<CastNCrew> = arrayListOf(),
    @SerializedName("crew") var crewList : List<CastNCrew> = arrayListOf()
)