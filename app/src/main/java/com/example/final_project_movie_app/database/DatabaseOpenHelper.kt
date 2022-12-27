package com.example.final_project_movie_app.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.final_project_movie_app.model.Movie

class DatabaseOpenHelper(
    context: Context?,
    name:String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
): SQLiteOpenHelper(context, name, factory, version){
    companion object{
        private var MOVIE_TABLE = "movie_table"
        private var REMINDER_TABLE = "reminder_table"

        private var MOVIE_ID = "movie_id"
        private var MOVIE_TITLE = "movie_name"
        private var MOVIE_RATING = "movie_rating"
        private var MOVIE_OVERVIEW = "movie_overview"
        private var MOVIE_IMG_POSTER = "movie_img_poster"
        private var MOVIE_DATE = "movie_date"
        private var MOVIE_ADULT = "movie_adult"
        private var MOVIE_FAVORITE = "movie_favorite"
        private var REMINDER_TIME_DISPLAY = "movie_reminder_time_display"
        private var REMINDER_TIME = "reminder_time"

        private var TAG = "MovieDB"
    }
    override fun onCreate(db: SQLiteDatabase?) {
        val createTableMovie = "CREATE TABLE $MOVIE_TABLE (" +
                "$MOVIE_ID INTERGER PRIMARY KEY, " +
                "$MOVIE_TITLE TEXT, " +
                "$MOVIE_OVERVIEW TEXT, " +
                "$MOVIE_RATING REAL, " +
                "$MOVIE_DATE TEXT, " +
                "$MOVIE_IMG_POSTER TEXT," +
                "$MOVIE_ADULT INTERGER, " +
                "$MOVIE_FAVORITE INTERGER)"
        val creatTableReminder = "CREATE TABLE $REMINDER_TABLE (" +
                "$MOVIE_ID INTERGER PRIMARY KEY, " +
                "$MOVIE_TITLE TEXT, " +
                "$MOVIE_OVERVIEW TEXT, " +
                "$MOVIE_RATING REAL, " +
                "$MOVIE_DATE TEXT, " +
                "$MOVIE_IMG_POSTER TEXT," +
                "$MOVIE_ADULT INTERGER, " +
                "$MOVIE_FAVORITE INTERGER, " +
                "$REMINDER_TIME_DISPLAY TEXT, " +
                "$REMINDER_TIME TEXT)"

        db!!.execSQL(createTableMovie)
        db.execSQL(creatTableReminder)
        Log.d("TAGTAGTAG","On Create")

    }

    fun getListMovie() : ArrayList<Movie>{
        val listMovie : ArrayList<Movie> = ArrayList()
        val selectQuery = "SELECT * FROM $MOVIE_TABLE"
        val db = this.readableDatabase
        val cursor : Cursor
        var movie : Movie
        try {
            cursor = db.rawQuery(selectQuery,null)
        }catch (e : SQLiteException){
            db.execSQL(selectQuery)
            return ArrayList()
        }
        if(cursor.moveToFirst()){
            do {
                movie = Movie(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getDouble(3),
                    cursor.getString(4),
                    cursor.getString(6),
                    cursor.getInt(6)==0,
                    cursor.getInt(7)==0
                )
                listMovie.add(movie)
            }while(cursor.moveToNext())
        }
        return listMovie
    }

    fun addMovie(movie: Movie): Int {
        val db = this.writableDatabase
        val contentValue = ContentValues()
        contentValue.put(MOVIE_ID,movie.id)
        contentValue.put(MOVIE_TITLE,movie.title)
        contentValue.put(MOVIE_OVERVIEW,movie.overview)
        contentValue.put(MOVIE_RATING,movie.voteAverage)
        contentValue.put(MOVIE_DATE,movie.releaseDate)
        contentValue.put(MOVIE_IMG_POSTER,movie.posterPath)
        if (movie.adult){
            contentValue.put(MOVIE_ADULT,0)
        }else{
            contentValue.put(MOVIE_ADULT,1)
        }
        contentValue.put(MOVIE_FAVORITE, 0)
        val success = db.insert(MOVIE_TABLE,null,contentValue)
        db.close()
        return success.toInt()
    }

    fun deleteMovie(movie: Movie): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        val id = movie.id
        contentValues.put(MOVIE_ID,id)
        val success = db.delete(MOVIE_TABLE,"$MOVIE_ID = $id",null)
        db.close()
        return success
    }



    fun getListReminder(): ArrayList<Movie> {
        val listMovie : ArrayList<Movie> = ArrayList()
        val selectQuery = "SELECT * FROM $REMINDER_TABLE"
        val db = this.readableDatabase
        val cursor : Cursor
        var movie : Movie
        try {
            cursor = db.rawQuery(selectQuery,null)
        }catch (e : SQLiteException){
            db.execSQL(selectQuery)
            return ArrayList()
        }
        if(cursor.moveToFirst()){
            do {
                movie = Movie(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getDouble(3),
                    cursor.getString(4),
                    cursor.getString(6),
                    cursor.getInt(6)==0,
                    cursor.getInt(7)==0,
                    cursor.getString(8),
                    cursor.getString(9)
                )
                listMovie.add(movie)
            }while(cursor.moveToNext())
        }
        return listMovie
    }

    fun addReminder(movie: Movie):Int{
        val db = this.writableDatabase
        val contentValue = ContentValues()
        contentValue.put(MOVIE_ID,movie.id)
        contentValue.put(MOVIE_TITLE,movie.title)
        contentValue.put(MOVIE_OVERVIEW,movie.overview)
        contentValue.put(MOVIE_RATING,movie.voteAverage)
        contentValue.put(MOVIE_DATE,movie.releaseDate)
        contentValue.put(MOVIE_IMG_POSTER,movie.posterPath)
        if (movie.adult){
            contentValue.put(MOVIE_ADULT,0)
        }else{
            contentValue.put(MOVIE_ADULT,1)
        }
        if (movie.isFavorite){
            contentValue.put(MOVIE_FAVORITE,0)
        }else{
            contentValue.put(MOVIE_FAVORITE,1)
        }
        contentValue.put(REMINDER_TIME,movie.reminderTime)
        contentValue.put(REMINDER_TIME_DISPLAY,movie.reminderTimeDisplay)
        val success = db.insert(REMINDER_TABLE,null,contentValue)
        db.close()
        return success.toInt()
    }

    fun updateReminder(movie : Movie) :Int{
        val db = this.writableDatabase
        val contentValue = ContentValues()
        contentValue.put(MOVIE_ID,movie.id)
        contentValue.put(MOVIE_TITLE,movie.title)
        contentValue.put(MOVIE_OVERVIEW,movie.overview)
        contentValue.put(MOVIE_RATING,movie.voteAverage)
        contentValue.put(MOVIE_DATE,movie.releaseDate)
        contentValue.put(MOVIE_IMG_POSTER,movie.posterPath)
        if (movie.adult){
            contentValue.put(MOVIE_ADULT,0)
        }else{
            contentValue.put(MOVIE_ADULT,1)
        }
        if (movie.isFavorite){
            contentValue.put(MOVIE_FAVORITE,0)
        }else{
            contentValue.put(MOVIE_FAVORITE,1)
        }
        contentValue.put(REMINDER_TIME_DISPLAY, movie.reminderTimeDisplay)
        contentValue.put(REMINDER_TIME,movie.reminderTime)
        val success = db.update(REMINDER_TABLE,contentValue,"movie_id =${movie.id}", arrayOf())
        return success
    }

    fun checkReminderExist(movieId :Int) : Int{
        val listMovie : ArrayList<Movie> = ArrayList()
        val selectQuery = "SELECT * FROM $REMINDER_TABLE WHERE $MOVIE_ID = $movieId"
        val db = this.readableDatabase
        val cursor : Cursor
        var movie : Movie
        try{
            cursor = db.rawQuery(selectQuery,null)
        }catch (e :SQLiteException){
            db.execSQL(selectQuery)
            return 0
        }
        if(cursor.moveToFirst()){
            do {
                movie = Movie(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getDouble(3),
                    cursor.getString(4),
                    cursor.getString(6),
                    cursor.getInt(6)==0,
                    cursor.getInt(7)==0,
                    cursor.getString(8),
                    cursor.getString(9)
                )
                listMovie.add(movie)
                if (listMovie.size > 0) return 1
            }while(cursor.moveToNext())
        }
        return 0
    }

    fun deleteMovieReminder(id : Int) : Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(MOVIE_ID,id)
        val success = db.delete(REMINDER_TABLE,"$MOVIE_ID = $id",null)
        db.close()
        return success
    }

    fun deleteAllRecord(){
        val db = this.writableDatabase
        db.execSQL("delete from $MOVIE_TABLE")
        db.close()
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        val dropTableMovie  = "DROP TABLE IF EXISTS $MOVIE_TABLE"
        val dropTableReminder = "DROP TABLE IF EXISTS $REMINDER_TABLE"
        p0!!.execSQL(dropTableMovie)
        p0.execSQL(dropTableReminder)
        onCreate(p0)
    }
}
