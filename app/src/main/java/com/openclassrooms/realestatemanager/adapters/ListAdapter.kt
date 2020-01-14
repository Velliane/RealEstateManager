package com.openclassrooms.realestatemanager.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.model.Property

class ListAdapter(private val context: Context) : RecyclerView.Adapter<ListAdapter.ListViewHolder>() {


    private var data: List<Property> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_list, parent, false)
        return ListViewHolder(view)
    }

    fun setData(newData: List<Property>) {
        data = newData
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return if (data.isNotEmpty()) {
            data.size
        } else {
            10
        }
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        if (data.isNotEmpty()) {
            holder.bind(data.get(position))
        } else {
            holder.type.setText("TYPE")
            holder.price.setText("150000")
            holder.location.setText("Unknown")
        }
    }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var photo: ImageView = itemView.findViewById(R.id.item_photo)
        var type: TextView = itemView.findViewById(R.id.item_type)
        var location: TextView = itemView.findViewById(R.id.item_location)
        var price: TextView = itemView.findViewById(R.id.item_price)


        fun bind(property: Property) {
            if (property != null) {
                type.setText(property.price)
                price.setText(property.price)
                location.setText("Unknown")
            }
        }

    }
}

