package com.khawi.ui.walkthrough

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.khawi.R
import com.khawi.base.loadImage
import com.khawi.model.Welcome

class ViewPager2Adapter(private val ctx: Context) :
    RecyclerView.Adapter<ViewPager2Adapter.ViewHolder>() {

    var images = mutableListOf<Welcome>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(ctx).inflate(R.layout.row_images_holder, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.images.loadImage(images[position].icon)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var images: ImageView

        init {
            images = itemView.findViewById(R.id.images)
        }
    }
}