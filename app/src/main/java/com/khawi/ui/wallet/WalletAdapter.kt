package com.khawi.ui.wallet

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.khawi.R


class WalletAdapter(
    private val ctx: Context,
    private val onClick: (item: String, position: Int) -> Unit
) :
    RecyclerView.Adapter<WalletAdapter.ViewHolder>() {

    var items = mutableListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(ctx).inflate(R.layout.row_transaction, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.transactionIcon.setImageResource(
            if (position % 2 == 0)
                R.drawable.transfer_up
            else
                R.drawable.transfer_down
        )

        holder.transactionName.text = "رسوم رحلة رقم #154645"
        holder.transactionAmount.text = "-50 SAR"
        holder.itemView.setOnClickListener {
            onClick.invoke(item, position)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var transactionIcon: ImageView
        var transactionName: TextView
        var transactionAmount: TextView

        init {
            transactionIcon = itemView.findViewById(R.id.transactionIcon)
            transactionName = itemView.findViewById(R.id.transactionName)
            transactionAmount = itemView.findViewById(R.id.transactionAmount)
        }
    }

}