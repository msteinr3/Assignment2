package com.example.assignment2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CustomAdapter(private val contactList: List<ContactInfo>) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    private lateinit var mlistener : onItemClickListener
    interface onItemClickListener{
        fun onItemClicked(position: Int)
    }

    fun setOnItemClickedListener(listener: onItemClickListener) {
        mlistener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.contact, parent, false)
        return ViewHolder(itemView, mlistener)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = contactList[position]
        holder.image.setImageResource(currentItem.image)
        holder.name.text = currentItem.name
        holder.phone.text = currentItem.phone
        //holder.email.text = currentItem.email
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return contactList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View, listener: onItemClickListener) : RecyclerView.ViewHolder(ItemView) {
        val image: ImageView = itemView.findViewById(R.id.image)
        val name: TextView = itemView.findViewById(R.id.name)
        val phone: TextView = itemView.findViewById(R.id.phone)
        //val email: TextView = itemView.findViewById(R.id.email)

        init {
            itemView.setOnClickListener {
                listener.onItemClicked(absoluteAdapterPosition)
            }
        }
    }
}

