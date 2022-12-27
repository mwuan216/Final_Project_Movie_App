package com.example.final_project_movie_app.listenercallback

import android.graphics.Bitmap

interface ProfileListener {
    fun onSaveProfile(name: String, email: String, dob: String, gender: String, bitmap: Bitmap?)

}
