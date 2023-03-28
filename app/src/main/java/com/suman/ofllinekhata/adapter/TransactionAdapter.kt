package com.suman.ofllinekhata.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.suman.ofllinekhata.R
import com.suman.ofllinekhata.interfaces.OnClickListener
import com.suman.ofllinekhata.model.TransactionModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TransactionAdapter(val list: ArrayList<TransactionModel>): RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {
    var mListener: OnClickListener? = null

    fun setOnClickListener(listener: OnClickListener) {
        mListener = listener
    }
    class ViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview){
        val date: TextView = itemView.findViewById(R.id.tv_tran_date)
        val amount: TextView = itemView.findViewById(R.id.tv_tran_amount)
        val desc: TextView = itemView.findViewById(R.id.tv_tran_desc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.sample_transaction, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = list[position]
        holder.date.text = SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.ENGLISH).format(Date(transaction.date))
        holder.amount.text = "\u20B9${transaction.amount}"
        holder.desc.text = if (transaction.description.equals("")) "NA" else transaction.description
        holder.itemView.setOnClickListener {
            mListener!!.onClick(transaction.id,"","")
        }
    }
}