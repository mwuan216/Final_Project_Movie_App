package com.example.final_project_movie_app.listenercallback

import com.example.final_project_movie_app.model.Movie

interface FavoriteListener {
    fun  onUpdateFromFavorite(movie: Movie)
}
