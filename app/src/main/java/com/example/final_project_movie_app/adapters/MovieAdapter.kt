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
import com.example.final_project_movie_app.model.Movie
import com.squareup.picasso.Picasso

class MovieAdapter(
    private var mListMovie : MutableList<Movie>,
    private var mScreenType : Int,
    private var mViewClickListener: View.OnClickListener,
    private var mIsFavoriteList : Boolean
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    fun updateData(listMovie : MutableList<Movie>){
        this.mListMovie = listMovie
    }
    //jsjkdfjk

    fun setTypeView(screenType : Int){
        this.mScreenType = screenType
    }

    fun setFavorite(listMovieFavorite : ArrayList<Movie>){
        for (i in 0 until listMovieFavorite.size){
            for(j in 0 until listMovieFavorite.size){
                if ( mListMovie[i].id == listMovieFavorite[j].id){
                    mListMovie[i].isFavorite = true
                    Log.d("vinh","22")
                }
            }
        }
    }

    fun setupMovieSetting (listMovie : ArrayList<Movie>, rate : String, releaseYear : String, sortBy : String){
        val rateConvert = rate.toInt().toDouble()

        listMovie.removeAll{it.voteAverage < rateConvert}

        val convertYear: Int? = if(releaseYear.length > 3) {
            releaseYear.substring(0,4).trim().toIntOrNull()
        } else {
            null
        }

        if (convertYear != null){
            listMovie.removeAll { it.releaseDate.substring(0,4).trim() != releaseYear }
        }

        if(sortBy == "Release Date")
            listMovie.sortByDescending { it.releaseDate }
        else if (sortBy == "Rating") {
            listMovie.sortByDescending { it.voteAverage }
        }
        mListMovie = listMovie
    }

    override fun getItemViewType(position: Int): Int {
        return if(!mIsFavoriteList && mListMovie.isNotEmpty()&&position == mListMovie.size-1){
            2
        }else{
            mScreenType
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (mScreenType == 1 ){
            ListViewHolder(
                mViewClickListener,mListMovie,
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.movie_item, parent,false))
        }else if(mScreenType == 0){
            GridViewHolder(mListMovie,
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.movie_item_grid, parent,false))
        }else {
            LoadViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.movie_item_load,parent,false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.tag = position
        holder.itemView.setOnClickListener(mViewClickListener)
        if (holder is GridViewHolder) {
            holder.bindData(position)
        }
        else {
            (holder as ListViewHolder).bindData(position)
        }
    }


    override fun getItemCount(): Int {
        return mListMovie.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeItemLoading() {
        if(mListMovie.isNotEmpty()){
            val lastPosition  = mListMovie.size-1
            mListMovie.removeAt(lastPosition)
            notifyDataSetChanged()
        }
    }
}

class LoadViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
}

class ListViewHolder (
    private var mViewClickListener: View.OnClickListener,
    private val movieList: MutableList<Movie>,
    itemview: View
) : RecyclerView.ViewHolder(itemview){
    private var imgMovie : ImageView = itemview.findViewById(R.id.img_movie)
    private var tvTitle : TextView = itemview.findViewById(R.id.tv_title)
    private var itemDate : TextView = itemview.findViewById(R.id.tv_date)
    private var itemRate : TextView = itemview.findViewById(R.id.tv_rate)
    private var itemAdult : ImageView = itemview.findViewById(R.id.img_adult)
    private var itemImgBt : ImageButton = itemview.findViewById(R.id.img_bt_favorite)
    private var itemOverView : TextView = itemview.findViewById(R.id.tv_overview)

    fun bindData(pos: Int){
        val movie = movieList[pos]
        val url = APIconstant.BASE_IMG_URL + movie.posterPath
        Picasso.get().load(url).error(R.drawable.default_movie).into(imgMovie)
        tvTitle.text = movie.title
        itemDate.text = movie.releaseDate
        "${movie.voteAverage}/10".also { itemRate.text = it }
        itemAdult.setImageResource(R.drawable.banner_adult)
        if(movie.adult) {
            itemAdult.visibility = View.VISIBLE
        }
        else{
            itemAdult.visibility = View.GONE
        }
        if(movie.isFavorite) {
            itemImgBt.setImageResource(R.drawable.ic_baseline_star_24)
        }else{
            itemImgBt.setImageResource(R.drawable.ic_baseline_star_outline_24)
        }
        itemOverView.text = movie.overview
        itemImgBt.tag = pos
        itemImgBt.setOnClickListener(mViewClickListener)
    }

}

class GridViewHolder (private var movieList: MutableList<Movie>, itemview: View) : RecyclerView.ViewHolder(itemview){
    private var imgMovie : ImageView = itemview.findViewById(R.id.img_movie_grid)
    private var tvTitle : TextView = itemview.findViewById(R.id.tv_title_grid)

    fun bindData(pos:Int){
        val movie = movieList[pos]
        val url = APIconstant.BASE_IMG_URL + movie.posterPath
        Picasso.get().load(url).into(imgMovie)
        tvTitle.text = movie.title
    }
}
