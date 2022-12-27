package com.example.final_project_movie_app.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.final_project_movie_app.R
import com.example.final_project_movie_app.adapters.ReminderAdapter
import com.example.final_project_movie_app.adapters.ReminderAdapter.Companion.REMINDER_ALL
import com.example.final_project_movie_app.database.DatabaseOpenHelper
import com.example.final_project_movie_app.listenercallback.DetailListener
import com.example.final_project_movie_app.listenercallback.ReminderListener
import com.example.final_project_movie_app.model.Movie

@Suppress("DEPRECATION")
class ReminderFragment(
    private var databaseOpenHelper: DatabaseOpenHelper,
    private var mReminderListener: ReminderListener,
    private var mReminderList : ArrayList<Movie>
    ): Fragment(){
    private lateinit var mReminderAdapter: ReminderAdapter
    private lateinit var mReminderRecyclerView: RecyclerView

    private lateinit var mDetailListener: DetailListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reminder, container, false)
        mReminderRecyclerView = view.findViewById(R.id.recycler_view_reminder)
        loadReminderList()
        setHasOptionsMenu(true)
        return view
    }
    fun loadReminderList(){
        mReminderAdapter = ReminderAdapter(mReminderList, REMINDER_ALL,databaseOpenHelper,mReminderListener)
        mReminderRecyclerView.adapter = mReminderAdapter
        mReminderRecyclerView.setHasFixedSize(true)
        mReminderRecyclerView.layoutManager = LinearLayoutManager(context)
    }

    fun setDetailListener(detailListener: DetailListener){
        this.mDetailListener = detailListener
    }
    fun setReminderListener(reminderListener: ReminderListener) {
        this.mReminderListener = reminderListener
    }
    fun updateReminderList(){
        loadReminderList()
    }
    override fun onPrepareOptionsMenu(menu: Menu) {
        val item = menu.findItem(R.id.change_layout)
        item.isVisible = false
    }
}
