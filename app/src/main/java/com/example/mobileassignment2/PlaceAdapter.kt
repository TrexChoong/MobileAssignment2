package com.example.mobileassignment2

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class PlaceAdapter(private val recordClickListener: RecordClickListener) : RecyclerView.Adapter<PlaceAdapter.ViewHolder>() {
    //Cached copy of contacts
    private var placeList = emptyList<Place>()

    class ViewHolder (view: View): RecyclerView.ViewHolder(view) {
        val textViewName: TextView = view.findViewById(R.id.textViewContactName)
        val textViewContact: TextView= view.findViewById(R.id.textViewContactVicinity)
    }

    internal fun setPlace(place: List<Place>){
        this.placeList = place
        Log.d("GET PLACE:", place.toString())
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //Create a new view, which define the UI of the list item
        val view =  LayoutInflater.from(parent.context).inflate(R.layout.record, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //Get element from the dataset at this position and replace the contents of the view with that element
        holder.textViewName.text = placeList[position].name
        holder.textViewContact.text = placeList[position].vicinity
        holder.itemView.setOnClickListener {
            //Item click event handler
            //Toast.makeText(it.context, "Contact name:" + contactList[position].name, Toast.LENGTH_SHORT).show()
            recordClickListener.onRecordClickListener(position)
        }
    }

    override fun getItemCount(): Int {
        return placeList.size
    }

}

interface RecordClickListener{
    //index= position of record selected by user
    fun onRecordClickListener(index: Int)
}