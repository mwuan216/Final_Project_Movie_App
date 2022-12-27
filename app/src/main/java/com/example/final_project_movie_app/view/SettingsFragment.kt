package com.example.final_project_movie_app.view

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import androidx.preference.*
import com.example.final_project_movie_app.R
import com.example.final_project_movie_app.constant.Constant
import com.example.final_project_movie_app.listenercallback.SettingListener

@Suppress("DEPRECATION")
class SettingsFragment : PreferenceFragmentCompat(),SharedPreferences.OnSharedPreferenceChangeListener{
    private lateinit var mCategoryListPref : ListPreference
    private lateinit var mRateSeekBarPref : SeekBarPreference
    private lateinit var mReleaseYearEditTextPref : EditTextPreference
    private lateinit var mSortListPref : ListPreference

    private lateinit var mSettingListener: SettingListener

    fun setSettingListener(settingListener: SettingListener){
        this.mSettingListener =settingListener
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences
            ?.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences
            ?.unregisterOnSharedPreferenceChangeListener(this)

    }
    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.fragment_setting,rootKey)
        setHasOptionsMenu(true)
        mCategoryListPref = findPreference(Constant.PREF_CATEGORY_KEY)!!
        mRateSeekBarPref = findPreference(Constant.PREF_RATE_KEY)!!
        mReleaseYearEditTextPref = findPreference(Constant.PREF_RELEASE_KEY)!!
        mSortListPref = findPreference(Constant.PREF_SORT_KEY)!!

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity!!)
        mCategoryListPref.summary =sharedPreferences.getString(Constant.PREF_CATEGORY_KEY,"Popular Movies")
        mRateSeekBarPref.summary = sharedPreferences.getInt(Constant.PREF_RATE_KEY,0).toString()
        mReleaseYearEditTextPref.summary = sharedPreferences.getString(Constant.PREF_RELEASE_KEY,"2022")
        mSortListPref.summary = sharedPreferences.getString(Constant.PREF_SORT_KEY,"None")

    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {
        val category = sharedPreferences.getString(Constant.PREF_CATEGORY_KEY,"Popular Movies")
        val rate = sharedPreferences.getInt(Constant.PREF_RATE_KEY, 0)
        val release = sharedPreferences.getString(Constant.PREF_RELEASE_KEY, "2022")
        val sort = sharedPreferences.getString(Constant.PREF_SORT_KEY,"None")

        when{
            key.equals(Constant.PREF_CATEGORY_KEY) -> {
                mCategoryListPref.summary = category
            }
            key.equals(Constant.PREF_RATE_KEY) ->{
                if (rate == 0 ) {
                    mRateSeekBarPref.summary = ""
                }
                else{
                    mRateSeekBarPref.summary = rate.toString()
                }
            }
            key.equals(Constant.PREF_RELEASE_KEY) ->{
                if(release.isNullOrEmpty()){
                    mReleaseYearEditTextPref.summary = ""
                }else {
                    mReleaseYearEditTextPref.summary = release
                }
            }
            key.equals(Constant.PREF_SORT_KEY) ->{
                if(sort=="None"){
                    mSortListPref.summary = ""
                }else {
                    mSortListPref.summary = sort
                }
            }
        }
        mSettingListener.onUpdateFromSetting()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val item = menu.findItem(R.id.change_layout)
        item.isVisible = false
    }
}
