package com.khawi.ui.request_details

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.khawi.R
import com.khawi.model.Day


class DaysAdapter(
    private val ctx: Context,
    private val onClick: (item: Day, position: Int) -> Unit
) :
    RecyclerView.Adapter<DaysAdapter.ViewHolder>() {

    var items = mutableListOf<Day>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(ctx).inflate(R.layout.row_day, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.selectIV.colorFilter = null
        if (item.select)
            holder.selectIV.setColorFilter(
                ContextCompat.getColor(ctx, R.color.main),
                android.graphics.PorterDuff.Mode.MULTIPLY
            )

        holder.dayName.text = item.name ?: ""

        holder.itemView.setOnClickListener {
            onClick.invoke(item, position)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var dayName: TextView
        var selectIV: ImageView


        init {
            dayName = itemView.findViewById(R.id.dayName)
            selectIV = itemView.findViewById(R.id.selectIV)
        }
    }

}