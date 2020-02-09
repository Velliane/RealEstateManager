package com.openclassrooms.realestatemanager.property.show

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.property.Property

class ListPropertyAdapter(private val listener: OnItemClickListener) : ListAdapter<Property, ListPropertyAdapter.ListViewHolder>(PropertyAdapterDiffCallback()) {


    private var data: List<Property> = ArrayList()
    private lateinit var onItemClickListener: OnItemClickListener


    interface OnItemClickListener {
        fun onItemClicked(id: String, position: Int)
    }

    fun setData(newData: List<Property>) {
        data = newData
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_list, parent, false)
        onItemClickListener = listener

        return ListViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(data[position])
    }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var photo: ImageView = itemView.findViewById(R.id.item_photo)
        var type: TextView = itemView.findViewById(R.id.item_type)
        var location: TextView = itemView.findViewById(R.id.item_location)
        var price: TextView = itemView.findViewById(R.id.item_price)
        var container: ConstraintLayout = itemView.findViewById(R.id.item_list_container)


        fun bind(property: Property) {

            type.text = property.type
            price.text = property.price.toString()
            //location.setText("Unknown")
            itemView.setOnClickListener {
                onItemClickListener.onItemClicked(property.id_property, adapterPosition)
            }
        }
    }

    private class PropertyAdapterDiffCallback: DiffUtil.ItemCallback<Property>() {

        override fun areItemsTheSame(oldItem: Property, newItem: Property): Boolean {
            return false
        }

        override fun areContentsTheSame(oldItem: Property, newItem: Property): Boolean {
            return false
        }
    }

}


