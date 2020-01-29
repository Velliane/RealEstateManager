package com.openclassrooms.realestatemanager.adapters

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.model.Property
import com.openclassrooms.realestatemanager.utils.Constants
import com.openclassrooms.realestatemanager.utils.getScreenOrientation

class ListAdapter(private val context: Context, private val listener: OnItemClickListener) : RecyclerView.Adapter<ListAdapter.ListViewHolder>() {


    private var data: List<Property> = ArrayList()
    private lateinit var onItemClickListener: OnItemClickListener
    private lateinit var mContext: Context
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var idSaved: String

    interface OnItemClickListener {
        fun onItemClicked(id: String)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_list, parent, false)
        mContext = context
        onItemClickListener = listener
        sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE)

        idSaved = sharedPreferences.getString(Constants.PREF_ID_PROPERTY, "")

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
            holder.bind(data[position])
        } else {
            holder.price.text = "150000"
            holder.type.text = "House"
            holder.location.text = "Unknown"
        }


    }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var photo: ImageView = itemView.findViewById(R.id.item_photo)
        var type: TextView = itemView.findViewById(R.id.item_type)
        var location: TextView = itemView.findViewById(R.id.item_location)
        var price: TextView = itemView.findViewById(R.id.item_price)
        var container: ConstraintLayout = itemView.findViewById(R.id.item_list_container)


        fun bind(property: Property) {
            if (getScreenOrientation(context.resources.configuration.orientation)) {
                if (idSaved == property.id_property) {
                    container.setBackgroundColor(context.resources.getColor(R.color.drawer_color))
                } else {
                    container.setBackgroundColor(context.resources.getColor(R.color.quantum_white_100))
                }
            }else{
                container.setBackgroundColor(context.resources.getColor(R.color.quantum_white_100))
            }
            type.text = property.type
            price.text = property.price.toString()
            //location.setText("Unknown")
            itemView.setOnClickListener {
                onItemClickListener.onItemClicked(property.id_property)
            }
        }
    }

}


