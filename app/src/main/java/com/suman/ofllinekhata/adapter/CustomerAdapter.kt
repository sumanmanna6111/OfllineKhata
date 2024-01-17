package com.suman.ofllinekhata.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.RecyclerView
import com.suman.ofllinekhata.R
import com.suman.ofllinekhata.interfaces.OnClickListener
import com.suman.ofllinekhata.room.entity.CustomerEntity
import java.util.Locale


class CustomerAdapter(var list: ArrayList<CustomerEntity>): RecyclerView.Adapter<CustomerAdapter.ViewHolder>() , Filterable{
    private var mListener: OnClickListener? = null
    fun setOnClickListener(listener: OnClickListener) {
        mListener = listener
    }
    var filterList = ArrayList<CustomerEntity>()
    init {
        filterList = list
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.sample_customer, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return filterList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val customer = filterList[position]
        holder.name.text = customer.name
        holder.number.text = customer.number
        holder.balance.text = "\u20B9${customer.amount}"
        holder.itemView.setOnClickListener {
            mListener!!.onClick(customer.id, customer.name, customer.number)
        }
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val name: TextView = itemView.findViewById(R.id.tv_customer_name)
        val number: TextView = itemView.findViewById(R.id.tv_customer_phone)
        val balance: TextView = itemView.findViewById(R.id.tv_customer_amount)

    }

    fun updateAdapter(mList: List<CustomerEntity>){
        list.clear()
        filterList.clear()
        list.addAll(mList)
        filterList = list
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    filterList = list
                } else {
                    val resultList = ArrayList<CustomerEntity>()
                    for (row in list) {
                        val name = row.name.lowercase(Locale.getDefault()) + row.number.lowercase(Locale.getDefault())
                        if (name.contains(constraint.toString().lowercase(Locale.getDefault()))) {
                            resultList.add(row)
                        }
                    }
                    filterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = filterList
                return filterResults
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filterList = results?.values as ArrayList<CustomerEntity>
                notifyDataSetChanged()
            }
        }
    }

    class DiffUtil : ItemCallback<CustomerEntity>(){
        override fun areItemsTheSame(oldItem: CustomerEntity, newItem: CustomerEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CustomerEntity, newItem: CustomerEntity): Boolean {
            return oldItem == newItem
        }

    }
}