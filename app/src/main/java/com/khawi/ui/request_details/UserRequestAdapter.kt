package com.khawi.ui.request_details

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.khawi.R
import com.khawi.base.loadImage
import com.khawi.model.Offer


class UserRequestAdapter(
    private val ctx: Context,
    private val onClick: (item: Offer, position: Int) -> Unit
) : RecyclerView.Adapter<UserRequestAdapter.ViewHolder>() {

    var items = mutableListOf<Offer>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(ctx).inflate(R.layout.row_user_request, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.username.text = item.user?.fullName ?: ""
        holder.userImage.loadImage(ctx, item.user?.image ?: "")
        holder.itemView.setOnClickListener {
            onClick.invoke(item, position)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var showDetails: TextView
        var username: TextView
        var userImage: ImageView


        init {
            showDetails = itemView.findViewById(R.id.showDetails)
            username = itemView.findViewById(R.id.username)
            userImage = itemView.findViewById(R.id.userImage)
        }
    }

}