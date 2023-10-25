package com.khawi.ui.main.orders

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.khawi.R


class OrderAdapter(
    private val ctx: Context,
    private val type:Int,
    private val onClick: (item: String, position: Int) -> Unit
) :
    RecyclerView.Adapter<OrderAdapter.ViewHolder>() {

    var items = mutableListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(ctx).inflate(R.layout.row_order, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.orderImg.setImageResource(R.drawable.logo_circle_white)
        holder.orderDate.text = "14 ربيع الأول، ١٤٤٥ هـ"
        holder.orderNumber.text = "رقم الرحلة: #12547"
        holder.orderDistance.text = "من: الرياض    إلى: جدة"
        holder.orderPrice.text = "50 SAR"
        when (type) {
            1 -> {
                holder.orderStatus.background = ContextCompat.getDrawable(ctx, R.drawable.bg_green_corner_12r)
                holder.orderStatus.setTextColor(ContextCompat.getColor(ctx, R.color.green2))
                holder.orderStatus.text = "مكتمل"
            }

            2 -> {
                holder.orderStatus.background = ContextCompat.getDrawable(ctx, R.drawable.bg_red_corner_12r)
                holder.orderStatus.setTextColor(ContextCompat.getColor(ctx, R.color.red2))
                holder.orderStatus.text = "ملغي"
            }

            else -> {
                holder.orderStatus.background = ContextCompat.getDrawable(ctx, R.drawable.bg_blue_corner_12r)
                holder.orderStatus.setTextColor(ContextCompat.getColor(ctx, R.color.blue))
                holder.orderStatus.text = "طلب مفتوح"
            }
        }

        holder.itemView.setOnClickListener {
            onClick.invoke(item, position)
        }
    }

    override fun getItemCount(): Int {
        return items.size
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