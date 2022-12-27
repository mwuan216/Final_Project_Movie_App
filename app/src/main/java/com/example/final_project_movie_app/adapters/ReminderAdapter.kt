package com.example.final_project_movie_app.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.final_project_movie_app.R
import com.example.final_project_movie_app.constant.APIconstant
import com.example.final_project_movie_app.database.DatabaseOpenHelper
import com.example.final_project_movie_app.listenercallback.ReminderListener
import com.example.final_project_movie_app.model.Movie
import com.squareup.picasso.Picasso

class ReminderAdapter(
    private var mMovieReminderList: ArrayList<Movie>,
    private var mType : Int,
    private var mDatabaseOpenHelper: DatabaseOpenHelper,
    private var mReminderListener: ReminderListener
) :RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>(){
    companion object{
        const val REMINDER_ALL = 0
        const val REMINDER_PROFILE = 1
    }



    fun setReminderListener(reminderListener: ReminderListener) {
        this.mReminderListener = reminderListener
    }

    fun updateData(mMovieList: ArrayList<Movie>) {
        this.mMovieReminderList = mMovieList
        notifyDataSetChanged()
    }
    fun deleteItem(movie: Movie){
        if(mDatabaseOpenHelper.deleteMovieReminder(movie.id)>-1){
            notifyDataSetChanged()
        }else{
            Log.d("TAG","remove fail")
        }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder{
        return ReminderViewHolder(
            mReminderListener,
            mType,
            LayoutInflater.from(parent.context).inflate(R.layout.reminder_item,parent,false)
        )

    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        holder.bindData(mMovieReminderList[position])
    }

    override fun getItemCount(): Int {
        return if(mType == REMINDER_ALL) mMovieReminderList.size
        else{
            if(mMovieReminderList.size > 3) 3 else mMovieReminderList.size
        }
    }
    inner class ReminderViewHolder(
        private var mReminderListener: ReminderListener,
        private var type: Int,
        itemView:View
    ) :RecyclerView.ViewHolder(itemView) {
        private var imgPoster : ImageView = itemView.findViewById(R.id.img_poster_reminder)
        private var title : TextView = itemView.findViewById(R.id.title_reminder)
        private var releaseDateText : TextView = itemView.findViewById(R.id.release_date)
        private var timeReminder : TextView = itemView.findViewById(R.id.time_reminder)
        private var reminderButton : ImageButton = itemView.findViewById(R.id.btn_reminder)

        @SuppressLint("SetTextI18n")
        fun bindData(movie: Movie){
            if(type == REMINDER_ALL) {
                val url = APIconstant.BASE_IMG_URL + movie.posterPath
                Picasso.get().load(url).error(R.drawable.default_movie).into(imgPoster)
                title.text = movie.title
                releaseDateText.text = "Release : " +movie.releaseDate
                timeReminder.text = movie.reminderTimeDisplay
                itemView.setOnClickListener { mReminderListener.onShowDetailReminder(movie) }
                reminderButton.setOnClickListener{mReminderListener.onButtonDeleteReminderClick(movie)}
            }
            else{
                imgPoster.visibility =View.GONE
                title.text = movie.title
                releaseDateText.text = "Release : " +movie.releaseDate
                timeReminder.text =  movie.reminderTimeDisplay
                itemView.setOnClickListener { mReminderListener.onShowDetailReminder(movie) }
                reminderButton.visibility = View.GONE
            }
        }
    }
}

