package com.example.final_project_movie_app.listenercallback

import com.example.final_project_movie_app.model.Movie

interface MovieListener {
    fun onUpdateFromMovie(movie: Movie,isFavorite : Boolean)
    fun onUpdateTitleFromMovie(title : String)
    fun onUpdateTitleFromDetail()
}
