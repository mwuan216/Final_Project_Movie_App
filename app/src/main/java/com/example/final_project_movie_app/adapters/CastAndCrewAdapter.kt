package com.example.final_project_movie_app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.final_project_movie_app.R
import com.example.final_project_movie_app.constant.APIconstant
import com.example.final_project_movie_app.model.CastNCrew
import com.squareup.picasso.Picasso

class CastAndCrewAdapter(
    private var mCastNCrewList: ArrayList<CastNCrew>
    ) : RecyclerView.Adapter<CastAndCrewAdapter.CastNCrewViewHolder>() {

    fun updateList(castNCrewlist: ArrayList<CastNCrew>){
        this.mCastNCrewList = castNCrewlist
        notifyDataSetChanged()
    }

    class CastNCrewViewHolder(itemview : View) : RecyclerView.ViewHolder (itemview){
        private var nameText = itemview.findViewById<TextView>(R.id.tv_cast_name)
        private var castImgView = itemview.findViewById<ImageView>(R.id.img_cast_detail)
        fun bindDataCastNCrew(castNCrew: CastNCrew) {
            val url = APIconstant.BASE_IMG_URL + castNCrew.profilePath
            Picasso.get().load(url).error(R.drawable.ic_baseline_person_24). into(castImgView)
            nameText.text = castNCrew.name
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CastNCrewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cast_crew_item,parent,false)
        return CastNCrewViewHolder(view)
    }

    override fun onBindViewHolder(holder: CastNCrewViewHolder, position: Int) {
        holder.bindDataCastNCrew(mCastNCrewList[position])
        }

    override fun getItemCount(): Int {
        return mCastNCrewList.size
    }
}

