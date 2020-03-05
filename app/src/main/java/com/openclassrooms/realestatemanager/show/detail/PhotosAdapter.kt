package com.openclassrooms.realestatemanager.show.detail

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.add_edit.Photo

class PhotosAdapter(private val context: Context): RecyclerView.Adapter<PhotosAdapter.PhotosViewHolder>() {

    private var data: List<Photo> = ArrayList()
    private lateinit var mContext: Context


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotosViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_photos, parent, false)
        mContext = context
        return PhotosViewHolder(view)
    }

    fun setData(newData: List<Photo>) {
        data = newData
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: PhotosViewHolder, position: Int) {
        if (data.isNotEmpty()){
            holder.bind(data[position])
        }else{
            Glide.with(context).load(R.drawable.no_image_available_64).centerCrop().into(holder.image)
        }

    }

    inner class PhotosViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        val image = itemView.findViewById<ImageView>(R.id.item_photo)
        val description = itemView.findViewById<TextView>(R.id.item_photo_txt)

        fun bind(photo: Photo){

            Glide.with(context).load(photo.uri).centerCrop().into(image)
            description.text = photo.description
        }

    }
}