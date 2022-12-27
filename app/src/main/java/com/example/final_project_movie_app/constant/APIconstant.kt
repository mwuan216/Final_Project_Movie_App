package com.example.final_project_movie_app.constant

class APIconstant {
    companion object{
        var BASE_URL = "https://api.themoviedb.org/"
        var BASE_IMG_URL = "https://image.tmdb.org/t/p/original/"
        var API_KEY = "e7631ffcb8e766993e5ec0c1f4245f93"
        var MOVIE_LIST_POPULAR_URL ="3/movie/popular?"
        var MOVIE_LIST_TOP_RATED_URL= "3/movie/top_rated?"
        var MOVIE_LIST_UPCOMING_URL = "3/movie/upcoming?"
        var MOVIE_LIST_NOEW_URL = "3/movie/now_playing?"
        var VIEW_TYPE_GRID = 0
        var VIEW_TYPE_LIST = 1
        var MOVIE_DETAIL_URL = "3/movie/{movieId}?"
        var CAST_URL = "3/movie/{movieId}/credits?"
        var SEARCH_MOVIE_URL = "3/search/movie?"
        const val PREFS_NAME = "prefname"

    }
}