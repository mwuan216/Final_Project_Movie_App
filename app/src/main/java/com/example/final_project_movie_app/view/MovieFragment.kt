package com.example.final_project_movie_app.view

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.final_project_movie_app.R
import com.example.final_project_movie_app.adapters.MovieAdapter
import com.example.final_project_movie_app.api.ApiInterface
import com.example.final_project_movie_app.api.RetrofitClient
import com.example.final_project_movie_app.constant.APIconstant
import com.example.final_project_movie_app.constant.Constant
import com.example.final_project_movie_app.database.DatabaseOpenHelper
import com.example.final_project_movie_app.listenercallback.BadgeListener
import com.example.final_project_movie_app.listenercallback.DetailListener
import com.example.final_project_movie_app.listenercallback.MovieListener
import com.example.final_project_movie_app.model.Movie
import com.example.final_project_movie_app.model.MovieList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MovieFragment(
    private var mDatabaseOpenHelper: DatabaseOpenHelper,

    ) : Fragment(),View.OnClickListener {
    private var mScreenType : Int= 1
    private lateinit var mMovieRecyclerView: RecyclerView
    private lateinit var mMovieAdapter: MovieAdapter
    private lateinit var mLinearLayoutManager: LinearLayoutManager
    private lateinit var mGridLayoutManager: GridLayoutManager
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mProgressLayout: RelativeLayout

    private lateinit var mMovieListDB : ArrayList<Movie>
    private lateinit var mHandler: Handler
    private lateinit var mMovieList: ArrayList<Movie>

    private lateinit var mSharedPreferences: SharedPreferences
    private lateinit var mRatePrefer : String
    private lateinit var mReleaseYearPref : String
    private lateinit var mSortByPref : String
    private lateinit var mMovieCategory : String
    private var mPage = 1

    private lateinit var mMovieListener : MovieListener
    private lateinit var mBadgeListener: BadgeListener
    private lateinit var mDetailListener: DetailListener
    fun setBadgeListener(badgeListener: BadgeListener){
        this.mBadgeListener = badgeListener
    }

    fun setHomeListener(movieListener: MovieListener){
        this.mMovieListener = movieListener
    }
    fun setDetailListener(detailListener: DetailListener){
        this.mDetailListener = detailListener
    }
    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_home, container, false)
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity!!)
        mSwipeRefreshLayout = v.findViewById(R.id.swipe_layout)
        mProgressLayout = v.findViewById(R.id.progress_bar_layout)
        mMovieRecyclerView = v.findViewById(R.id.list_recycler_view)
        mHandler = Handler(Looper.getMainLooper())
        mMovieListDB = mDatabaseOpenHelper.getListMovie()
        loadDataBySetting()
        loadMovieList()
        mHandler.postDelayed({
            getListMovieFromApi(isRefresh = false, isLoadMore = false)
        },1000)

        mSwipeRefreshLayout.setOnRefreshListener {
            mHandler.postDelayed({
                mSwipeRefreshLayout.isRefreshing = false
                loadMovieList()
                getListMovieFromApi(isRefresh = true, isLoadMore = false)
            },1000)
        }
        loadMore()
        return v
    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onClick(view: View) {
        when (view.id){
            R.id.img_bt_favorite ->{
                val pos = view.tag as Int
                val movieItem = mMovieList[pos]
                if(movieItem.isFavorite){
                    if(mDatabaseOpenHelper.deleteMovie(movieItem)>-1){
                        movieItem.isFavorite = false
                        mMovieAdapter.notifyItemChanged(pos)
                        mBadgeListener.onUpdateBadgeNumber(false)
                        mMovieListener.onUpdateFromMovie(movieItem,false)
                    }else{
                        Toast.makeText(context,"Remove Fail ${movieItem.id}",Toast.LENGTH_SHORT).show()
                    }
                }else{
                    if(mDatabaseOpenHelper.addMovie(movieItem)>-1){
                        movieItem.isFavorite = true
                        mMovieAdapter.notifyItemChanged(pos)
//                        mMovieListDB.add(movieItem)
                        mBadgeListener.onUpdateBadgeNumber(true)
                        mMovieListener.onUpdateFromMovie(movieItem,true)
                    }else{
                        Toast.makeText(context,"Add Fail ${movieItem.id}",Toast.LENGTH_SHORT).show()
                    }
                }
            }
            R.id.movie_item ->{
                val pos = view.tag as Int
                val movieItem = mMovieList[pos]
                val bundle = Bundle()
                bundle.putSerializable("movieDetail",movieItem)
                val detailFragment = DetailFragment(mDatabaseOpenHelper)
                detailFragment.setBadgeListener(mBadgeListener)
                detailFragment.setDetailListener(mDetailListener)
                detailFragment.setMovieListener(mMovieListener)
                detailFragment.arguments = bundle
                activity!!.supportFragmentManager.beginTransaction().apply {
                    add(R.id.frg_movie_content,detailFragment,"frg_detail")
                    addToBackStack(null)
                    commit()
                    mMovieListener.onUpdateTitleFromMovie(movieItem.title)
                }
            }
        }
    }

    private fun loadMovieList() {
        mPage = 1
        mMovieList = ArrayList()
        mLinearLayoutManager = LinearLayoutManager(activity)
        mGridLayoutManager = GridLayoutManager(activity,2)

        mMovieAdapter = MovieAdapter(mMovieList,mScreenType,this,false)
        if (mScreenType == 1){
            mMovieRecyclerView.layoutManager = mLinearLayoutManager
        }else{
            mMovieRecyclerView.layoutManager = mGridLayoutManager
        }
        mMovieRecyclerView.setHasFixedSize(true)
        mMovieRecyclerView.adapter = mMovieAdapter
    }

    fun setListMovieByCondition(){
        loadDataBySetting()
        loadMovieList()
        mHandler.postDelayed({
            getListMovieFromApi(isRefresh = false, isLoadMore = false)
        },1000)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun changViewHome(){
        if (mMovieRecyclerView.layoutManager == mGridLayoutManager){
            mScreenType = 1
            mMovieRecyclerView.layoutManager = mLinearLayoutManager
        }else{
            mScreenType = 0
            mMovieRecyclerView.layoutManager = mGridLayoutManager
        }
        mMovieAdapter.setTypeView(mScreenType)
        mMovieRecyclerView.adapter = mMovieAdapter
        mMovieAdapter.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateMovieList(movie : Movie, isFavorite : Boolean){
        mMovieList[findById(movie.id)].isFavorite = isFavorite
        mMovieAdapter.notifyDataSetChanged()
    }
    private fun findById(id: Int): Int {
        var pos = -1
        for (i in 0 until mMovieList.size-1){
            if(mMovieList[i].id==id){
                pos = i
                break
            }
        }
        return pos
    }
    private fun loadMore(){
        mMovieRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (isLastItemDisplaying(recyclerView)){
                    mProgressLayout.visibility = View.VISIBLE
                    mHandler.postDelayed({
                        getListMovieFromApi(isRefresh = false, isLoadMore = true)
                        mProgressLayout.visibility = View.GONE
                    },1000)
                }
            }
        })
    }
    private fun isLastItemDisplaying(recyclerView: RecyclerView): Boolean {
        if (recyclerView.adapter!!.itemCount !=0 ){
            val lastVisiblePosition =
                (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
            if(lastVisiblePosition != RecyclerView.NO_POSITION && lastVisiblePosition == recyclerView.adapter!!.itemCount-1){
                return true
            }
        }
        return false
    }
    private fun loadDataBySetting() {
        mMovieCategory = mSharedPreferences.getString(Constant.PREF_CATEGORY_KEY,"popular").toString()
        mRatePrefer = mSharedPreferences.getInt(Constant.PREF_RATE_KEY,10).toString()
        mReleaseYearPref = mSharedPreferences.getString(Constant.PREF_RELEASE_KEY,"").toString()
        mSortByPref = mSharedPreferences.getString(Constant.PREF_SORT_KEY,"").toString()
    }
    private fun getListMovieFromApi(isRefresh: Boolean,isLoadMore: Boolean) {
        if(isLoadMore){
            mPage += 1
        }else{
            if (!isRefresh){
                mProgressLayout.visibility = View.VISIBLE
            }
        }
        val retrofit : ApiInterface =
            RetrofitClient().getRetrofitInstance().create(ApiInterface::class.java)
        val category = when(mMovieCategory){
            "Popular Movies" -> "popular"
            "Top Rate Movies" -> "top_rated"
            "Upcoming Movies" -> "upcoming"
            "Now Playing Movies"->"now_playing"
            else -> "popular"
        }
        val retrofitData =
            retrofit.getMovieList(category, APIconstant.API_KEY,"$mPage")
        retrofitData.enqueue(object : Callback<MovieList?> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<MovieList?>?, response: Response<MovieList?>?){
//                mMovieAdapter.removeItemLoading()
                val responseBody = response!!.body()
                val listMovieResult = responseBody?.results as ArrayList<Movie>
                mMovieList.addAll(listMovieResult)
                mMovieAdapter.setFavorite(mMovieListDB)
                mMovieAdapter.notifyDataSetChanged()
                mMovieAdapter.setupMovieSetting(
                    mMovieList,
                    mRatePrefer,
                    mReleaseYearPref,
                    mSortByPref
                )

//                if (mPage < responseBody.totalPages){
//                    val loaderItem = Movie(0,"0","0",0.0,"0","0",
//                        adult = false,
//                        isFavorite = false,
//                        reminderTimeDisplay = "0",
//                        reminderTime = "0"
//                    )
//                    mMovieList.add(loaderItem)
//                }


//                mMovieAdapter.setDataPreference(mMovieList, mRatePrefer, mReleaseYearPref, mSortByPref)
                if (!isLoadMore && !isRefresh){
                    mProgressLayout.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<MovieList?>?, t: Throwable?) {
                if (isLoadMore){
                    mPage = -1
                }else{
                    if (!isRefresh){
                        mProgressLayout.visibility = View.GONE
                    }
                }
            }
        })
    }
}