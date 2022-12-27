package com.example.final_project_movie_app.listenercallback

import com.example.final_project_movie_app.model.Movie

interface ReminderListener {
    fun onShowDetailReminder(movie: Movie)
    fun onRemoveReminder(movie: Movie)
    fun onButtonDeleteReminderClick(movie: Movie)
}