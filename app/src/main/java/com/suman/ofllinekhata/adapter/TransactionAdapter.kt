package com.suman.ofllinekhata.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.suman.ofllinekhata.R
import com.suman.ofllinekhata.interfaces.OnClickListener
import com.suman.ofllinekhata.model.TransactionModel
import com.suman.ofllinekhata.room.entity.TransactionEntity
import java.text.SimpleDateFormat
import java.util.*

class TransactionAdapter(private val list: ArrayList<TransactionEntity>): RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {
    private var mListener: OnClickListener? = null

    fun setOnClickListener(listener: OnClickListener) {
        mListener = listener
    }
    inner class ViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview){
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
        if (transaction.received == 1){
            holder.date.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            holder.amount.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            holder.desc.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        }else{
            holder.date.paintFlags = Paint.LINEAR_TEXT_FLAG
            holder.amount.paintFlags = Paint.LINEAR_TEXT_FLAG
            holder.desc.paintFlags = Paint.LINEAR_TEXT_FLAG
        }
        holder.date.text = SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.ENGLISH).format(Date(transaction.time))
        holder.amount.text = "\u20B9${transaction.amount}"
        holder.desc.text = if (transaction.description.equals("")) "NA" else transaction.description
        holder.itemView.setOnClickListener {
            mListener!!.onClick(transaction.id,"","")
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList : List<TransactionEntity>){
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }
}