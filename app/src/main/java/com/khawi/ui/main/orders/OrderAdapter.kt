package com.khawi.ui.main.orders

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.khawi.R
import com.khawi.base.cancelledKey
import com.khawi.base.finishedKey
import com.khawi.model.Order


class OrderAdapter(
    private val ctx: Context,
    private val onClick: (item: Order, position: Int) -> Unit
) : ListAdapter<Order, OrderAdapter.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Order>() {
            override fun areItemsTheSame(oldItem: Order, newItem: Order) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Order, newItem: Order) =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(ctx).inflate(R.layout.row_order, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = currentList[position]

        holder.orderImg.setImageResource(R.drawable.logo_circle_white)
        holder.orderDate.text = item.dtDate ?: ""
        holder.orderNumber.text = "${ctx.getString(R.string.trip_number)}: #${item.orderNo}"
        holder.orderDistance.text =
            "${ctx.getString(R.string.from)}: ${item.fAddress}    ${ctx.getString(R.string.to)}: ${item.tAddress}"
        val price = item.price ?: "0.0"
        holder.orderPrice.text = "$price ${ctx.getString(R.string.currancy)}"
        when (item.status) {
            finishedKey -> {
                holder.orderStatus.background =
                    ContextCompat.getDrawable(ctx, R.drawable.bg_green_corner_12r)
                holder.orderStatus.setTextColor(ContextCompat.getColor(ctx, R.color.green2))
                holder.orderStatus.text = ctx.getString(R.string.finished)
            }

            cancelledKey -> {
                holder.orderStatus.background =
                    ContextCompat.getDrawable(ctx, R.drawable.bg_red_corner_12r)
                holder.orderStatus.setTextColor(ContextCompat.getColor(ctx, R.color.red2))
                holder.orderStatus.text = ctx.getString(R.string.cancelled)
            }

            else -> {
                holder.orderStatus.background =
                    ContextCompat.getDrawable(ctx, R.drawable.bg_blue_corner_12r)
                holder.orderStatus.setTextColor(ContextCompat.getColor(ctx, R.color.blue))
                holder.orderStatus.text = ctx.getString(R.string.open_order)
            }
        }

        holder.itemView.setOnClickListener {
            onClick.invoke(item, position)
        }
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var orderImg: ImageView
        var orderStatus: TextView
        var orderDate: TextView
        var orderNumber: TextView
        var orderDistance: TextView
        var orderPrice: TextView


        init {
            orderImg = itemView.findViewById(R.id.orderImg)
            orderStatus = itemView.findViewById(R.id.orderStatus)
            orderDate = itemView.findViewById(R.id.orderDate)
            orderNumber = itemView.findViewById(R.id.orderNumber)
            orderDistance = itemView.findViewById(R.id.orderDistance)
            orderPrice = itemView.findViewById(R.id.orderPrice)
        }
    }

}