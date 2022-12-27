package com.example.final_project_movie_app.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.final_project_movie_app.R
import com.example.final_project_movie_app.database.DatabaseOpenHelper

const val notificationID =1
const val channelID = "channel"
const val titleExtra = "titleExtra"
const val messageExtra = "messageExtra"
const val idExtra = "idExtra"
class AlarmReceiver : BroadcastReceiver(){
    override fun onReceive(context: Context, intent: Intent) {
        val bundle = intent.extras
        if(bundle!=null){
            val id = bundle.getInt(idExtra)
            val databaseOpenHelper = DatabaseOpenHelper(context,"movie_database",null,1)

            if (databaseOpenHelper.deleteMovieReminder(id) > 0){
                val notification = NotificationCompat.Builder(context, channelID)
                    .setSmallIcon(R.drawable.flash_screen)
                    .setContentTitle(bundle.getString(titleExtra, "no title"))
                    .setContentText(bundle.getString(messageExtra, "no content"))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .build()
                val manager = NotificationManagerCompat.from(context)
                manager.notify(notificationID,notification)
            }else{
                Log.d("AlarmReceiver ","Remove fail")
            }
        }


    }

}