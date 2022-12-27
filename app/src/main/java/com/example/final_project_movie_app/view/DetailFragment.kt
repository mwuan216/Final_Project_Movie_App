package com.example.final_project_movie_app.view

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.final_project_movie_app.R
import com.example.final_project_movie_app.adapters.CastAndCrewAdapter
import com.example.final_project_movie_app.api.ApiInterface
import com.example.final_project_movie_app.api.RetrofitClient
import com.example.final_project_movie_app.constant.APIconstant
import com.example.final_project_movie_app.database.DatabaseOpenHelper
import com.example.final_project_movie_app.listenercallback.BadgeListener
import com.example.final_project_movie_app.listenercallback.DetailListener
import com.example.final_project_movie_app.listenercallback.MovieListener
import com.example.final_project_movie_app.model.CastCrewList
import com.example.final_project_movie_app.model.CastNCrew
import com.example.final_project_movie_app.model.Movie
import com.example.final_project_movie_app.util.NotificationUtil
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

@Suppress("DEPRECATION")
class DetailFragment(private var databaseOpenHelper: DatabaseOpenHelper) :Fragment(),
    View.OnClickListener{
    private lateinit var mMovie : Movie
    private lateinit var mCastNCrewList : ArrayList<CastNCrew>

    private lateinit var mFavoriteBtn : ImageButton
    private lateinit var mDateText : TextView
    private lateinit var mRateText : TextView
    private lateinit var mOverViewText : TextView
    private lateinit var mCastRecyclerView : RecyclerView
    private lateinit var mTimeText : TextView
    private lateinit var mPostImg : ImageView
    private lateinit var mReminderButton : Button
    private lateinit var mCastAndCrewAdapter: CastAndCrewAdapter

    private var mSaveDay = 0
    private var mSaveMonth = 0
    private var mSaveYear = 0
    private var mSaveHour = 0
    private var mSaveMinus = 0

    private lateinit var mBadgeListener : BadgeListener
    private lateinit var mDetailListener: DetailListener
    private lateinit var mMovieListener: MovieListener

    //SET UP LISTENER
    fun setBadgeListener(badgeListener: BadgeListener) {
        this.mBadgeListener = badgeListener
    }

    fun setMovieListener(movieListener: MovieListener){
        this.mMovieListener = movieListener
    }

    fun setDetailListener(detailListener: DetailListener) {
        this.mDetailListener = detailListener
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_details, container, false)
        val bundle = this.arguments
        if (bundle!=null){
            mMovie  = bundle.getSerializable("movieDetail") as Movie
        }
        mCastNCrewList = arrayListOf()

        mTimeText = view.findViewById(R.id.time_reminder_detail)
        mFavoriteBtn = view.findViewById(R.id.img_btn_detail)
        mRateText = view.findViewById(R.id.tv_rate_detail)
        mDateText = view.findViewById(R.id.tv_date_detail)
        mOverViewText = view.findViewById(R.id.tv_overview_detail)
        mCastRecyclerView = view.findViewById(R.id.recycler_view_castNcrew)
        mPostImg = view.findViewById(R.id.img_view_detail)
        mReminderButton = view.findViewById(R.id.btn_detail)
        mCastAndCrewAdapter = CastAndCrewAdapter(mCastNCrewList)

        if (mMovie.isFavorite){
            mFavoriteBtn.setImageResource(R.drawable.ic_baseline_star_24)
        }else{
            mFavoriteBtn.setImageResource(R.drawable.ic_baseline_star_outline_24)
        }
        mRateText.setText("${mMovie.voteAverage}/10",TextView.BufferType.EDITABLE)
        mDateText.text = mMovie.releaseDate
        mOverViewText.text = mMovie.overview
        val url = APIconstant.BASE_IMG_URL+ mMovie.posterPath
        Picasso.get().load(url).error(R.drawable.default_movie).into(mPostImg)
        mTimeText.text = mMovie.reminderTimeDisplay
        mReminderButton.setOnClickListener{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                createReminder()
            }
        }
        mFavoriteBtn.setOnClickListener(this)
        val layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        mCastRecyclerView.layoutManager = layoutManager
        mCastRecyclerView.setHasFixedSize(true)
        mCastRecyclerView.adapter = mCastAndCrewAdapter
        getCastAndCrewFromApi()
        setHasOptionsMenu(true)
        return view
    }

    override fun onClick(p0: View?) {
        when(p0!!.id){
            R.id.img_btn_detail ->{
                if(mMovie.isFavorite){
                    if (databaseOpenHelper.deleteMovie(mMovie)>-1){
                        mMovie.isFavorite = false
                        mFavoriteBtn.setImageResource(R.drawable.ic_baseline_star_outline_24)
                        mDetailListener.onUpdateFromDetail(mMovie,false)
                        mBadgeListener.onUpdateBadgeNumber(false)
                    }else{
                        Toast.makeText(context,"Remove Fail ${mMovie.id}", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    if(databaseOpenHelper.addMovie(mMovie)>-1){
                        mMovie.isFavorite = true
                        mFavoriteBtn.setImageResource(R.drawable.ic_baseline_star_24)
                        mDetailListener.onUpdateFromDetail(mMovie,true)
                        mBadgeListener.onUpdateBadgeNumber(true)
                    }else{
                        Toast.makeText(context,"Add Fail ${mMovie.id}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createReminder() {
        val currentDateTime = Calendar.getInstance()
        val startYear = currentDateTime.get(Calendar.YEAR)
        val startMonth = currentDateTime.get(Calendar.MONTH)
        val startDate = currentDateTime.get(Calendar.DAY_OF_MONTH)
        val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
        val startMinute = currentDateTime.get(Calendar.MINUTE)

        DatePickerDialog(requireContext(),{_,year,month,day ->
            TimePickerDialog(requireContext(),{_,hour,minute ->
                val pickerDateTime = Calendar.getInstance()
                pickerDateTime.set(year,month,day,hour,minute)
                currentDateTime.set(year,month,day,hour,minute)

                val reminderTime : Long = currentDateTime.timeInMillis
                val reminderTimeDisplay = "$hour:$minute   $day/$month/$year"

                mMovie.reminderTimeDisplay = reminderTimeDisplay
                mMovie.reminderTime = reminderTime.toString()
                mTimeText.text = mMovie.reminderTimeDisplay

                if(databaseOpenHelper.checkReminderExist(mMovie.id)>0) {
                    if (databaseOpenHelper.updateReminder(mMovie)>0){
                        Toast.makeText(context,"Update reminder time successfully",Toast.LENGTH_SHORT).show()
                        mDetailListener.onUpdateReminder(mMovie)
                        NotificationUtil().cancelNotification(mMovie.id,requireContext())
                        NotificationUtil().createNotification(mMovie,reminderTime,requireContext())
                    }else{
                        Toast.makeText(context,"Update reminder fail ",Toast.LENGTH_SHORT).show()
                    }
                }else{
                    if(databaseOpenHelper.addReminder(mMovie)>0){
                        Toast.makeText(context,"Add reminder successfully",Toast.LENGTH_SHORT).show()
                        mDetailListener.onAddReminder(mMovie)
                        NotificationUtil().createNotification(mMovie,reminderTime,requireContext())
                    }else{
                        Toast.makeText(context,"Add reminder fail",Toast.LENGTH_SHORT).show()
                    }
                }

            },startHour,startMinute,true).show()
        },startYear,startMonth,startDate).show()
    }

    fun updateMovie(movieId : Int) {
        if(movieId == mMovie.id){
            mMovie.isFavorite = false
            mFavoriteBtn.setImageResource(R.drawable.ic_baseline_star_outline_24)
        }
    }

    private fun getCastAndCrewFromApi() {
        val retrofit : ApiInterface = RetrofitClient().getRetrofitInstance().create(ApiInterface::class.java)
        val retrofitData = retrofit.getCastAndCrew(mMovie.id,APIconstant.API_KEY)

        retrofitData.enqueue(object : Callback<CastCrewList> {
            override fun onResponse(call: Call<CastCrewList>?, response: Response<CastCrewList>?) {
                val responseBody =  response!!.body()
                mCastNCrewList.addAll(responseBody!!.castList)
                mCastNCrewList.addAll(responseBody.crewList)
                mCastAndCrewAdapter.updateList(mCastNCrewList)
            }
            override fun onFailure(call: Call<CastCrewList>?, t: Throwable?) {
            }

        })
    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onPause() {
        super.onPause()
        mMovieListener.onUpdateTitleFromDetail()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val item = menu.findItem(R.id.change_layout)
        item.isVisible = false
    }
}
