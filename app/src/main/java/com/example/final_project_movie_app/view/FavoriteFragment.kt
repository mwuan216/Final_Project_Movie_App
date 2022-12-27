package com.example.final_project_movie_app.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.final_project_movie_app.R
import com.example.final_project_movie_app.adapters.MovieAdapter
import com.example.final_project_movie_app.database.DatabaseOpenHelper
import com.example.final_project_movie_app.listenercallback.BadgeListener
import com.example.final_project_movie_app.listenercallback.FavoriteListener
import com.example.final_project_movie_app.model.Movie

@Suppress("DEPRECATION")
class FavoriteFragment(
    private var mDatabaseOpenHelper: DatabaseOpenHelper,
    private var mMovieFavoriteList : ArrayList<Movie>
) : Fragment(), View.OnClickListener{
    private lateinit var mMovieRecyclerView: RecyclerView
    private lateinit var mMovieAdapter: MovieAdapter


    private lateinit var mBadgeListener: BadgeListener
    private lateinit var mFavoriteListener: FavoriteListener



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_favorite, container, false)

        mMovieRecyclerView = view.findViewById(R.id.list_recycler_view_favorite)
        loadFavoriteList()
        setHasOptionsMenu(true)

        return view
    }

    fun setFavoriteListener(favoriteListener: FavoriteListener) {
        this.mFavoriteListener = favoriteListener
    }

    fun setBadgeListener(badgeListener: BadgeListener) {
        this.mBadgeListener = badgeListener
    }

    private fun loadFavoriteList() {
        mMovieAdapter = MovieAdapter(mMovieFavoriteList,1,this,true)
        mMovieRecyclerView.layoutManager = LinearLayoutManager(activity)
        mMovieRecyclerView.setHasFixedSize(true)
        mMovieRecyclerView.adapter=mMovieAdapter
    }

    fun updateFavoriteList(movie: Movie,isFavorite :Boolean){
        var pos = -1
        val size = mMovieFavoriteList.size
        for (i in 0 until size){
            if(mMovieFavoriteList[i].id == movie.id){
                pos = i
                break
            }
        }
        if(isFavorite){
            mMovieFavoriteList.add(movie)
        }else{
            if(pos != -1){
                mMovieFavoriteList.removeAt(pos)
            }
        }
        loadFavoriteList()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onClick(view: View) {
        when(view.id){
            R.id.img_bt_favorite ->{
                val position = view.tag as Int
                val movieItem = mMovieFavoriteList[position]
                if(mDatabaseOpenHelper.deleteMovie(movieItem)>-1){
                    mMovieFavoriteList.remove(movieItem)
                    mMovieAdapter.notifyDataSetChanged()
                    mBadgeListener.onUpdateBadgeNumber(false)
                    mFavoriteListener.onUpdateFromFavorite(movieItem)
                }else{
                    Toast.makeText(context,"Remove Fail $movieItem",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val item = menu.findItem(R.id.change_layout)
        item.isVisible = false
    }
}
