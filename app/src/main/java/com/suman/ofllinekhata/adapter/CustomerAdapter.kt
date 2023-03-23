package com.suman.ofllinekhata.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.suman.ofllinekhata.R
import com.suman.ofllinekhata.interfaces.OnClickListener
import com.suman.ofllinekhata.model.CustomerModel

class CustomerAdapter(val list: ArrayList<CustomerModel>): RecyclerView.Adapter<CustomerAdapter.ViewHolder>(){
    var mListener: OnClickListener? = null

    fun setOnClickListener(listener: OnClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.sample_customer, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val customer = list[position]
        holder.name.text = customer.name
        holder.number.text = "${customer.number}"
        holder.balance.text = "\u20B9${customer.balance}"
        holder.itemView.setOnClickListener {
            mListener!!.onClick(customer.id!!)
        }
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val name: TextView = itemView.findViewById(R.id.tv_customer_name)
        val number: TextView = itemView.findViewById(R.id.tv_customer_number)
        val balance: TextView = itemView.findViewById(R.id.tv_customer_balance)

    }
}