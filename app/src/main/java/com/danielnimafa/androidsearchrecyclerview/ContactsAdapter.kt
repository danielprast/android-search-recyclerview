package com.danielnimafa.androidsearchrecyclerview

import android.content.Context
import android.widget.TextView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions


/**
 * Created by danielnimafa on 16/03/18.
 */
class ContactsAdapter(val context: Context,
                      var contactList: ArrayList<Contact>,
                      val itemTap: (Contact) -> Unit) : RecyclerView.Adapter<ContactsAdapter.MyViewHolder>(), Filterable {

    var contactListFiltered: ArrayList<Contact>

    init {
        contactListFiltered = contactList
    }

    class MyViewHolder(view: View, contactListFiltered: ArrayList<Contact>, val itemTap: (Contact) -> Unit) : RecyclerView.ViewHolder(view) {

        var name: TextView = view.findViewById(R.id.name)
        var phone: TextView = view.findViewById(R.id.phone)
        var thumbnail: ImageView = view.findViewById(R.id.thumbnail)

        init {
            view.setOnClickListener {
                itemTap(contactListFiltered[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.user_row_item, parent, false)
        return MyViewHolder(view, contactListFiltered, itemTap)
    }

    override fun onBindViewHolder(holder: MyViewHolder?, position: Int) {
        val contact = contactListFiltered[position]
        holder?.also { h ->
            with(contact) {
                h.name.text = name
                h.phone.text = phone
                Glide.with(context).load(image)
                        .apply(RequestOptions.circleCropTransform()).into(h.thumbnail)
            }
        }
    }

    override fun getItemCount(): Int = contactListFiltered.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString() ?: ""
                if (charString.isEmpty()) contactListFiltered = contactList else {
                    val filteredList = ArrayList<Contact>()
                    contactList
                            .filter { it.name!!.toLowerCase().contains(charString.toLowerCase()) or it.phone!!.contains(constraint!!) }
                            .forEach { filteredList.add(it) }
                    contactListFiltered = filteredList
                }
                return FilterResults().apply { values = contactListFiltered }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                contactListFiltered = results?.values as ArrayList<Contact>
                notifyDataSetChanged()
            }
        }
    }
}