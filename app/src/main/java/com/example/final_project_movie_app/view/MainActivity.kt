@file:Suppress("DEPRECATION")

package com.example.final_project_movie_app.view

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.example.final_project_movie_app.R
import com.example.final_project_movie_app.adapters.ReminderAdapter
import com.example.final_project_movie_app.adapters.ReminderAdapter.Companion.REMINDER_PROFILE
import com.example.final_project_movie_app.adapters.ViewPagerAdapter
import com.example.final_project_movie_app.broadcastreceiver.channelID
import com.example.final_project_movie_app.constant.APIconstant.Companion.PREFS_NAME
import com.example.final_project_movie_app.database.DatabaseOpenHelper
import com.example.final_project_movie_app.listenercallback.*
import com.example.final_project_movie_app.model.Movie
import com.example.final_project_movie_app.util.ImageConverter
import com.example.final_project_movie_app.util.NotificationUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout


class MainActivity : AppCompatActivity(), FavoriteListener, BadgeListener, MovieListener,
    DetailListener,  ReminderListener,ProfileListener,SettingListener, BackListener{

    private var mIsGridView : Boolean = false
    private lateinit var mMovieFragment: MovieFragment
    private lateinit var mFavoriteFragment: FavoriteFragment
    private lateinit var mSettingFragment: SettingsFragment
    private lateinit var mAboutFragment: AboutFragment
    private lateinit var mDetailFragment : DetailFragment
    private lateinit var mReminderFragment : ReminderFragment
    private lateinit var mEditProfileFragment : EditProfileFragment

    private lateinit var mViewPager : ViewPager
    private lateinit var mTabLayout : TabLayout
    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var mNavigationView : NavigationView
    private lateinit var mIconList : MutableList<Int>
    private lateinit var mTabTitleList : MutableList<String>
    private lateinit var mViewPagerAdapter: ViewPagerAdapter

    private lateinit var mMovieFavoriteList: ArrayList<Movie>
    private lateinit var mReminderList: ArrayList<Movie>

    private lateinit var mDatabaseOpenHelper : DatabaseOpenHelper
    private var mFavoriteCount : Int = 0


//navigation view
    private lateinit var mEditBt : Button
    private lateinit var mAvatarImg : ImageView
    private lateinit var mHeaderLayout : View
    private lateinit var mShowAllReminderButton: Button
    private lateinit var mNameText : TextView
    private lateinit var mEmailNameText : TextView
    private lateinit var mDateOfBirthText : TextView
    private lateinit var mGenderText : TextView
    private lateinit var mSharePreferences: SharedPreferences
//    //take photo
    private var imgBitmap: Bitmap?=null
    private val imgConverter : ImageConverter = ImageConverter()
    //reminder recycler view
    private lateinit var mReminderAdapter: ReminderAdapter
    private lateinit var mReminderRecyclerView : RecyclerView

    private lateinit var mMovieListType : String



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(supportActionBar != null){
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24)
            supportActionBar!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.back_ground)))
        }
//
        mMovieListType = "upcoming"
        mTabTitleList = mutableListOf("Movie","Favorite","Setting","About")
        mIconList = mutableListOf(
        R.drawable.ic_baseline_home_24,
        R.drawable.ic_baseline_favorite_24,
        R.drawable.ic_baseline_settings_24,
        R.drawable.ic_baseline_info_24,
        )
        getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        mSharePreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

        mDatabaseOpenHelper = DatabaseOpenHelper(this,"movie_database",null,1)
        mReminderList = mDatabaseOpenHelper.getListReminder()
        mMovieFavoriteList = mDatabaseOpenHelper.getListMovie()
        mFavoriteCount = mMovieFavoriteList.size

        //navigation view
        mNavigationView = findViewById(R.id.navigation_view)
        mHeaderLayout = mNavigationView.getHeaderView(0)
        mNavigationView.bringToFront()
//      fragment
        mDetailFragment = DetailFragment(mDatabaseOpenHelper)
        mFavoriteFragment = FavoriteFragment(mDatabaseOpenHelper,mMovieFavoriteList)
        mSettingFragment = SettingsFragment()
        mAboutFragment = AboutFragment()
        mMovieFragment = MovieFragment(mDatabaseOpenHelper)
        mEditProfileFragment = EditProfileFragment()
        mReminderFragment = ReminderFragment(mDatabaseOpenHelper,this,mReminderList)
//      call back listener
        mMovieFragment.setBadgeListener(this)
        mMovieFragment.setHomeListener(this)
        mMovieFragment.setDetailListener(this)

        mFavoriteFragment.setFavoriteListener(this)
        mFavoriteFragment.setBadgeListener(this)

        mDetailFragment.setBadgeListener(this)
        mDetailFragment.setDetailListener(this)
        mDetailFragment.setMovieListener(this)

        mEditProfileFragment.setProfileListener(this)
        mEditProfileFragment.setMovieListener(this)
        mEditProfileFragment.setBackListener(this)

        mReminderFragment.setReminderListener(this)
        mReminderFragment.setDetailListener(this)

        mSettingFragment.setSettingListener(this)
        setUpTabs()
        setUpDrawerLayout()

    // button in navigation view
        mEditBt = mHeaderLayout.findViewById(R.id.btn_edit_profile)
        mShowAllReminderButton = mHeaderLayout.findViewById(R.id.btn_show)
        mAvatarImg = mHeaderLayout.findViewById(R.id.img_avatar)
        mNameText = mHeaderLayout.findViewById(R.id.tv_name)
        mDateOfBirthText = mHeaderLayout.findViewById(R.id.tv_dateofbirth)
        mEmailNameText = mHeaderLayout.findViewById(R.id.tv_mail)
        mGenderText = mHeaderLayout.findViewById(R.id.tv_gender)

        loadDataProfile()

        //Edit profile fragment
        mEditBt.setOnClickListener{
            if(this.mDrawerLayout.isDrawerOpen(GravityCompat.START)){
                this.mDrawerLayout.closeDrawer(GravityCompat.START)
                supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24)
            }
            val bundle = Bundle()
            bundle.putString("name",mNameText.text.toString())
            bundle.putString("email",mEmailNameText.text.toString())
            bundle.putString("dob",mDateOfBirthText.text.toString())
            bundle.putString("gender",mGenderText.text.toString())
            try {
                bundle.putString("imgBitMapString",mSharePreferences.getString("profileImg","No Data"))
            }catch (e : Exception){
            }
            mEditProfileFragment.arguments = bundle
            supportActionBar!!.title = "Edit Profile"
            supportFragmentManager.beginTransaction().apply {
                add(R.id.layout_main,mEditProfileFragment,"frg_edit_profile")
                addToBackStack(null)
                commit()
            }
            Toast.makeText(this,"Open Edit Profile",Toast.LENGTH_SHORT).show()
        }

        mShowAllReminderButton.setOnClickListener{
            supportActionBar!!.title = "Reminder"
            if(this.mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                this.mDrawerLayout.closeDrawer(GravityCompat.START)
                supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24)
            }
            if(!mReminderFragment.isAdded){
                supportFragmentManager.beginTransaction().apply {
                    add(R.id.frg_movie_content,mReminderFragment,"frg_reminder")
                    addToBackStack(null)
                    commit()
                }
            }else {
                val oldFrg3 = supportFragmentManager.findFragmentByTag("frg_reminder")
                if(oldFrg3!=null){
                    supportFragmentManager.beginTransaction().apply {
                        remove(oldFrg3)
                        addToBackStack(null)
                        commit()
                    }
                    supportFragmentManager.beginTransaction().apply {
                        add(R.id.frg_main,mReminderFragment,"frg_reminder")
                        addToBackStack(null)
                    }
                }
            }
        }
        //handler recycler view reminder
        val linearLayoutManager = LinearLayoutManager(this)
        mReminderAdapter = ReminderAdapter(mReminderList,REMINDER_PROFILE,mDatabaseOpenHelper,this)
        mReminderAdapter.setReminderListener(this)
        mReminderRecyclerView = mHeaderLayout.findViewById(R.id.recycler_view_reminder_navigation)
        mReminderRecyclerView.layoutManager =linearLayoutManager
        mReminderRecyclerView.setHasFixedSize(true)
        mReminderRecyclerView.adapter = mReminderAdapter
        createNotificationChannel()
    }

    override fun backToHome() {
        val oldFrag = supportFragmentManager.findFragmentByTag("frg_edit_profile")
        supportFragmentManager.beginTransaction().apply {
            remove(oldFrag!!)
            addToBackStack(null)
            commit()
        }
    }

    //CREATE NOTIFICATION CHANNEL
    @SuppressLint("UseRequireInsteadOfGet")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(channelID,"Movie!!!",NotificationManager.IMPORTANCE_DEFAULT)
        channel.description = "Time to watch a movie <3"

        val notificationManager: NotificationManager = this.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    private fun loadDataProfile() {
        mNameText.text = mSharePreferences.getString("profileName","No data")
        mEmailNameText.text = mSharePreferences.getString("profileEmail","No data")
        mDateOfBirthText.text = mSharePreferences.getString("profileDob","No data")
        mGenderText.text = mSharePreferences.getString("profileGender", "No data")
        try{
            mAvatarImg.setImageBitmap(
                imgConverter.decodeBase64(
                    mSharePreferences.getString("profileImg","No data")
                )
            )
        }catch (e:Exception){
            mAvatarImg.setImageResource(R.drawable.ic_baseline_person_24)
        }
    }

    private fun setUpDrawerLayout() {
        mDrawerLayout = findViewById(R.id.drawer_layout)
    }
    //SET UP TAB & TITLE
    private fun setUpTabs(){
        mViewPager = findViewById(R.id.viewpager)
        mTabLayout = findViewById(R.id.tab_layout)

        mViewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        mViewPagerAdapter.addFragment(mMovieFragment,"Home")
        mViewPagerAdapter.addFragment(mFavoriteFragment,"Favorite")
        mViewPagerAdapter.addFragment(mSettingFragment,"Setting")
        mViewPagerAdapter.addFragment(mAboutFragment,"About")
        mViewPager.offscreenPageLimit = 4

        mViewPager.adapter = mViewPagerAdapter
        mTabLayout.setupWithViewPager(mViewPager)

        val countFragment = mViewPagerAdapter.count-1
        for(i in 0..countFragment){
            mTabLayout.getTabAt(i)!!.setCustomView(R.layout.tab_item)
            val tabview =mTabLayout.getTabAt(i)!!.customView
            val titleTab = tabview!!.findViewById<TextView>(R.id.tab_title)
            titleTab.text = mTabTitleList[i]
            val iconTab = tabview.findViewById<ImageView>(R.id.tab_icon)
            iconTab.setImageResource(mIconList[i])
            if(i == 1){
                val badge = tabview.findViewById<TextView>(R.id.tab_badge)
                badge.visibility = View.VISIBLE
                badge.setText(
                    "$mFavoriteCount",
                    TextView.BufferType.EDITABLE
                )
            }

        }
        setTitleFragment()
    }

    private fun setTitleFragment(){
        mViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }
            override fun onPageSelected(position: Int) {
                mTabLayout.nextFocusRightId = position
                supportActionBar!!.title = mTabTitleList[position]
            }
            override fun onPageScrollStateChanged(state: Int) {
            }

        })
    }

    //SET UP TOOLBAR
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.change_layout
            -> {
                mIsGridView = !mIsGridView
                if(mIsGridView){
                    item.setIcon(R.drawable.ic_baseline_view_list_24)
                }else{
                    item.setIcon(R.drawable.ic_baseline_grid_on_24)
                }
                mMovieFragment.changViewHome()
            }
            android.R.id.home->{
                if(!this.mDrawerLayout.isDrawerOpen(GravityCompat.START)){
                    val navigationView = findViewById<NavigationView>(R.id.navigation_view)
                    supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_baseline_keyboard_return_24)
                    mDrawerLayout.openDrawer(navigationView)
                }else{
                    supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24)
                    this.mDrawerLayout.closeDrawer(GravityCompat.START)
                }
            }
            R.id.action_home -> mViewPager.currentItem = 0
            R.id.action_favorite -> mViewPager.currentItem = 1
            R.id.action_setting -> mViewPager.currentItem = 2
            R.id.action_info -> mViewPager.currentItem = 3
        }
        return super.onOptionsItemSelected(item)
    }
    //LISTENER FROM FRAGMENTS
    //from movie when click favorite button
    override fun onUpdateFromMovie(movie:Movie,isFavorite : Boolean) {
        mFavoriteFragment.updateFavoriteList(movie,isFavorite)
    }
    //update title when show detail
    override fun onUpdateTitleFromMovie(title: String) {
        supportActionBar!!.title = title
    }
    //update title when back to movie fragment from detail fragment
    override fun onUpdateTitleFromDetail() {
        supportActionBar!!.title = "Movie"
    }
    //update badge
    override fun onUpdateBadgeNumber(isFavorite: Boolean) {
        val tabView = mTabLayout.getTabAt(1)!!.customView
        val badgeText = tabView!!.findViewById<TextView>(R.id.tab_badge)
        if (isFavorite){
            badgeText.setText("${++mFavoriteCount}",TextView.BufferType.EDITABLE)
        }else{
            badgeText.setText("${--mFavoriteCount}",TextView.BufferType.EDITABLE)
        }
    }
    //update when remove item favorite
    override fun onUpdateFromFavorite(movie: Movie) {
        mMovieFragment.updateMovieList(movie,false)
        val detailFragment = supportFragmentManager.findFragmentByTag("frg_detail")
        if(detailFragment!=null){
            detailFragment as DetailFragment
            detailFragment.updateMovie(movie.id)
        }
    }
    //update when click favorite button from detail fragment
    override fun onUpdateFromDetail(movie: Movie,isFavorite: Boolean){
        mMovieFragment.updateMovieList(movie,isFavorite)
        mFavoriteFragment.updateFavoriteList(movie,isFavorite)
    }
    //update movie list from setting fragment
    override fun onUpdateFromSetting(){
        mMovieFragment.setListMovieByCondition()
    }

    //REMINDER
    //add reminder item
    override fun onAddReminder(movie: Movie) {
        mReminderList.add(movie)
        mReminderAdapter.updateData(mReminderList)
    }
    //update list reminder
    override fun onUpdateReminder(mMovie: Movie) {
        for(i in 0 until mReminderList.size){
            if(mReminderList[i].id==mMovie.id){
                mReminderList.removeAt(i)
                mReminderList.add(i,mMovie)
            }
        }
        mReminderAdapter.updateData(mReminderList)
    }
    //remove reminder item
    override fun onRemoveReminder(movie: Movie) {
        val index = mReminderList.indexOf(movie)
        mReminderList.removeAt(index)
        mReminderAdapter.deleteItem(movie)
        mReminderFragment.updateReminderList()
    }
    //event when click button delete of reminder item
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onButtonDeleteReminderClick(movie: Movie) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Confirm delete")
            .setMessage("Are you sure to delete this reminder ?")
            .setPositiveButton("Confirm") { _, _ ->
                if (mDatabaseOpenHelper.deleteMovieReminder(movie.id)>-1){
                    NotificationUtil().cancelNotification(movie.id,this)
                    onRemoveReminder(movie)
                    if(mReminderList.size<=0){
                        supportFragmentManager.beginTransaction().remove(mReminderFragment).commit()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    //click item reminder -> show detail of movie
    override fun onShowDetailReminder(movie: Movie){
        val bundle = Bundle()
        bundle.putSerializable("movieDetail",movie)
        mDetailFragment.arguments = bundle
        if (this.mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            this.mDrawerLayout.closeDrawer(GravityCompat.START)
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24)
        }
        // check if reminder fragment already exits
        val oldReminderFragment = supportFragmentManager.findFragmentByTag("frg_reminder")
        if (oldReminderFragment!=null){
            supportFragmentManager.beginTransaction().apply {
                remove(oldReminderFragment)
                addToBackStack(null)
                commit()
            }
        }
        // check if detail fragment is already exist
        if(supportFragmentManager.findFragmentByTag("frg_detail")==null){
            openDetailFromReminderItem(movie,mDetailFragment)
        }else{
            // if true => remove old frg and add a new one
            val oldDetailFragment =  supportFragmentManager.findFragmentByTag("frg_detail")
            if (oldDetailFragment!=null){
                supportFragmentManager.beginTransaction().apply {
                    remove(oldDetailFragment)
                    addToBackStack(null)
                    commit()
                }
            }
            val detailFragment = DetailFragment(mDatabaseOpenHelper)
            detailFragment.setDetailListener(this)
            detailFragment.setBadgeListener(this)
            detailFragment.setMovieListener(this)
            detailFragment.arguments = bundle
            openDetailFromReminderItem(movie,detailFragment)
        }
    }
    //check current page to add detail fragment
    private fun openDetailFromReminderItem(movie: Movie,detailFragment: DetailFragment){
        when (mViewPager.currentItem) {
            0 -> {
                supportActionBar!!.title = movie.title
                supportFragmentManager.beginTransaction().apply {
                    add(R.id.frg_movie_content, detailFragment, "frg_detail")
                    addToBackStack(null)
                    commit()
                }
            }
            1 -> {
                supportActionBar!!.title = movie.title
                supportFragmentManager.beginTransaction().apply {
                    add(R.id.frg_favorite, detailFragment, "frg_detail")
                    addToBackStack(null)
                    commit()
                }
            }
            2 -> {
                supportActionBar!!.title = movie.title
                supportFragmentManager.beginTransaction().apply {
                    add(R.id.frg_setting_content,detailFragment, "frg_detail")
                    addToBackStack(null)
                    commit()
                }
            }
            3 -> {
                supportActionBar!!.title = movie.title
                supportFragmentManager.beginTransaction().apply {
                    add(R.id.frg_about_content, detailFragment, "frg_detail")
                    addToBackStack(null)
                    commit()
                }
            }
        }
    }
    //save profile from editProfileFragment

    //EDIT PROFILE
    override fun onSaveProfile(
        name: String,
        email: String,
        dob: String,
        gender: String,
        bitmap: Bitmap?
    ) {
        val edit = mSharePreferences.edit()
        edit.putString("profileName",name)
        edit.putString("profileEmail",email)
        edit.putString("profileDob",dob)
        edit.putString("profileGender",gender)
        if(bitmap!=null) {
            edit.putString("profileImg", imgConverter.encodeBase64(bitmap))
        }
        this.imgBitmap = bitmap
        mAvatarImg.setImageBitmap(imgBitmap)
        edit.apply()
        loadDataProfile()
        Toast.makeText(this,"Save profile successfully",Toast.LENGTH_SHORT).show()
        mEditProfileFragment.onDestroy()
    }

    override fun onBackPressed() {
        if (this.mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            this.mDrawerLayout.closeDrawer(GravityCompat.START)
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24)
        }else{
            super.onBackPressed()
        }
    }
}