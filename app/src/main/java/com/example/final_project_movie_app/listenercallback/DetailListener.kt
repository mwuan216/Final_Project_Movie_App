package com.example.final_project_movie_app.listenercallback

import com.example.final_project_movie_app.model.Movie

interface DetailListener {
    fun onUpdateFromDetail(movie: Movie, isFavorite: Boolean)
    fun onAddReminder(movie: Movie)
    fun onUpdateReminder(mMovie: Movie)
}
