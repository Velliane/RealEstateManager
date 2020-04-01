package com.openclassrooms.realestatemanager.show.detail

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.add_edit.Photo

class PhotosAdapter(private val context: Context, private val listener: OnItemClickListener): RecyclerView.Adapter<PhotosAdapter.PhotosViewHolder>() {

    private var data: List<Photo> = ArrayList()
    private lateinit var mContext: Context
    private lateinit var onItemClickListener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClicked(photo: Photo, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotosViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_photos, parent, false)
        mContext = context
        onItemClickListener = listener
        return PhotosViewHolder(view)
    }

    fun setData(newData: List<Photo>) {
        data = newData
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: PhotosViewHolder, position: Int) {
        if (data.isNotEmpty()){
            holder.bind(data[position], onItemClickListener)
        }else{
            Glide.with(context).load(R.drawable.no_image_available_64).centerCrop().into(holder.image)
        }
    }

    inner class PhotosViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        val image: ImageView = itemView.findViewById(R.id.item_photo)
        val description: TextView = itemView.findViewById(R.id.item_photo_txt)
        private val container: ConstraintLayout = itemView.findViewById(R.id.photo_list_container)

        fun bind(photo: Photo, listener: OnItemClickListener){

            Glide.with(context).load(photo.uri).centerCrop().into(image)
            description.text = photo.description
            if (photo.isSelected) {
                container.setBackgroundResource(R.color.colorAccent)
            } else {
                container.setBackgroundResource(R.color.quantum_white_100)
            }
            image.setOnClickListener {
                listener.onItemClicked(photo, adapterPosition)
            }
        }
    }

}