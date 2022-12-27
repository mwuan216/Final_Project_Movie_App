package com.example.final_project_movie_app.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.example.final_project_movie_app.broadcastreceiver.AlarmReceiver
import com.example.final_project_movie_app.broadcastreceiver.idExtra
import com.example.final_project_movie_app.broadcastreceiver.messageExtra
import com.example.final_project_movie_app.broadcastreceiver.titleExtra
import com.example.final_project_movie_app.model.Movie

class NotificationUtil {
    @RequiresApi(Build.VERSION_CODES.M)
    fun createNotification(movie: Movie, reminderTime : Long, context : Context){
        val intent = Intent(context,AlarmReceiver::class.java)
        val bundle = Bundle()

        bundle.putInt(idExtra,movie.id)
        bundle.putString(titleExtra,movie.title)
        bundle.putString(messageExtra,"Release : ${movie.releaseDate}  ${movie.voteAverage}/10")
        intent.putExtras(bundle)

        val pendingIntent = PendingIntent.getBroadcast(
            context,movie.id,intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager =context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                reminderTime,
                pendingIntent
            )
        }
    }
    @RequiresApi(Build.VERSION_CODES.M)
    fun cancelNotification(movieId : Int, context: Context){
        val intent = Intent (context,AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            movieId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }
}