package com.khawi.ui.wallet

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.khawi.R
import com.khawi.model.Wallet


class WalletAdapter(
    private val ctx: Context,
    private val onClick: (item: Wallet, position: Int) -> Unit
) : ListAdapter<Wallet, WalletAdapter.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Wallet>() {
            override fun areItemsTheSame(oldItem: Wallet, newItem: Wallet) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Wallet, newItem: Wallet) =
                oldItem == newItem
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(ctx).inflate(R.layout.row_transaction, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = currentList[position]

        val amount = item.total ?: 0.0
        holder.transactionIcon.setImageResource(
//            if (item.details == "شحن المحفظة الالكترونية")
            if (amount > 0)
                R.drawable.transfer_up
            else
                R.drawable.transfer_down
        )

        holder.transactionName.text = item.details ?: ""
//            if (amount > 0)
//                ctx.getString(R.string.added_amount)
//            else
//                "${ctx.getString(R.string.amount_trip_number)} ${item.orderNo}"
        holder.transactionAmount.text = "$amount ${ctx.getString(R.string.currancy)}"

        holder.itemView.setOnClickListener {
            onClick.invoke(item, position)
        }
    }

    override fun getItemCount(): Int {
        return currentList.size
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