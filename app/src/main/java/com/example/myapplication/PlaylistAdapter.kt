package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.playlist_item_row.view.*



class PlaylistAdapter(private val myDataset: ArrayList<playlistItem>,val context: Context) :
    RecyclerView.Adapter<PlaylistAdapter.MyViewHolder>() {

    interface ItemClick{
        fun onClick(view: View, position: Int)
    }

    var itemClick: ItemClick? = null


    class MyViewHolder(v: View, val context: Context) : RecyclerView.ViewHolder(v){
        private var view: View = v

        fun bind(item: playlistItem) {
            GlideApp.with(context).load(item.albumImageURL).into(view.albumImage)
            view.musicName.text = item.musicName
            view.singerName.text = item.singerName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            PlaylistAdapter.MyViewHolder {
        val inflatedView = LayoutInflater.from(parent.context)
            .inflate(R.layout.playlist_item_row, parent, false)
        return PlaylistAdapter.MyViewHolder(inflatedView, context)
    }


    override fun onBindViewHolder(holder: PlaylistAdapter.MyViewHolder, position: Int) {

        val item = myDataset[position]

        holder.apply {
            bind(item)
            itemView.tag = item
        }

        if(itemClick !=null){
            holder?.itemView.setOnClickListener { v ->
                itemClick?.onClick(v, position)
            }
        }


    }

    override fun getItemCount() = myDataset.size
}