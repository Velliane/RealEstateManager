package com.openclassrooms.realestatemanager.show.list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.persistableBundleOf
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.utils.Utils
import com.openclassrooms.realestatemanager.utils.getScreenOrientation
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class ListPropertyAdapter(private val listener: OnItemClickListener, private val context: Context) : ListAdapter<PropertyModelForList, ListPropertyAdapter.ListViewHolder>(PropertyAdapterDiffCallback()) {


    private var data: List<PropertyModelForList> = ArrayList()
    private lateinit var onItemClickListener: OnItemClickListener
    private var currencySelected = 0


    interface OnItemClickListener {
        fun onItemClicked(id: String, position: Int)
    }

    fun setData(newData: List<PropertyModelForList>, currency: Int) {
        data = newData
        currencySelected = currency
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
        holder.bind(data[position], onItemClickListener, currencySelected)
    }

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var photo: ImageView = itemView.findViewById(R.id.item_photo)
        var type: TextView = itemView.findViewById(R.id.item_type)
        var location: TextView = itemView.findViewById(R.id.item_location)
        var price: TextView = itemView.findViewById(R.id.item_price)
        val soldIcon: ImageView = itemView.findViewById(R.id.drawable_sold_icon)
        private var container: ConstraintLayout = itemView.findViewById(R.id.item_list_container)
        private var progressBar: ProgressBar = itemView.findViewById(R.id.item_list_progress)


        fun bind(property: PropertyModelForList, onItemClickListener: OnItemClickListener, currency: Int) {

            itemView.isClickable = false
            type.text = property.type
            val format = NumberFormat.getCurrencyInstance()
            format.maximumFractionDigits = 0
            if(currency == 0){
                format.currency = Currency.getInstance("EUR")
                price.text = format.format(property.price.toInt())
            }else{
                format.currency = Currency.getInstance("USD")
                val priceInDollar = Utils.convertEuroToDollar(property.price.toInt()).toString()
                price.text = format.format(priceInDollar.toInt())
            }

            val propertyPhoto = property.photo
            if(propertyPhoto != null){
                Glide.with(itemView.context).load(propertyPhoto.uri).centerCrop().into(photo)
            }else{
                Glide.with(itemView.context).load(R.drawable.no_image_available_64).centerCrop().into(photo)
            }
            location.text = property.location
            //-- Set background of property selected if orientation is landscape --//
            if(getScreenOrientation(itemView.context.resources.configuration.orientation)) {
                if (property.isSelected) {
                    container.setBackgroundResource(R.color.drawer_color)
                } else {
                    container.setBackgroundResource(R.color.quantum_white_100)
                }
            }else{
                container.setBackgroundResource(R.color.quantum_white_100)
            }

            if(!property.inSale){
                soldIcon.visibility = View.VISIBLE
            }else{
                soldIcon.visibility = View.GONE
            }

            progressBar.visibility = View.GONE
            itemView.isClickable = true
            itemView.setOnClickListener {
                onItemClickListener.onItemClicked(property.propertyId, adapterPosition)
            }
        }
    }

    private class PropertyAdapterDiffCallback: DiffUtil.ItemCallback<PropertyModelForList>() {

        override fun areItemsTheSame(oldItem: PropertyModelForList, newItem: PropertyModelForList): Boolean {
            return oldItem.propertyId == newItem.propertyId
        }

        override fun areContentsTheSame(oldItem: PropertyModelForList, newItem: PropertyModelForList): Boolean {
            return oldItem.type == newItem.type
                    && oldItem.price == newItem.price
                    && oldItem.location == newItem.location
                    && oldItem.photo?.uri == newItem.photo?.uri
        }
    }

}


