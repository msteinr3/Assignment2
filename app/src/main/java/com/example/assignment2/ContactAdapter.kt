package com.example.assignment2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ContactAdapter(private val contactList: List<Contact>) : RecyclerView.Adapter<ContactAdapter.ViewHolder>() {

    private lateinit var mlistener : ItemClickListener

    interface ItemClickListener{
        fun onItemClicked(position: Int)
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View, listener: ItemClickListener) : RecyclerView.ViewHolder(ItemView) {
        val name: TextView = itemView.findViewById(R.id.name)
        val phone: TextView = itemView.findViewById(R.id.phone)
        //val email: TextView = itemView.findViewById(R.id.email)

        init {
            itemView.setOnClickListener {
                listener.onItemClicked(absoluteAdapterPosition)
            }
        }
    }

    fun setOnItemClickedListener(listener: ItemClickListener) {
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
        holder.name.text = currentItem.name
        holder.phone.text = currentItem.phone
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return contactList.size
    }
}

