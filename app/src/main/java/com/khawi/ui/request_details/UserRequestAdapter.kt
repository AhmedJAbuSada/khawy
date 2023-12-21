package com.khawi.ui.request_details

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.khawi.R
import com.khawi.base.acceptOfferKey
import com.khawi.base.loadImage
import com.khawi.model.Offer
import com.willy.ratingbar.ScaleRatingBar


class UserRequestAdapter(
    private val ctx: Context,
    private val orderType: Int,
    private val isRequest: Boolean = false,
    private val canCall: Boolean = false,
    private val userId:String? = null,
    private val onClick: (item: Offer, position: Int, type: ClickType) -> Unit
) : RecyclerView.Adapter<UserRequestAdapter.ViewHolder>() {

    var items = mutableListOf<Offer>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(ctx).inflate(R.layout.row_user_request, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        val user = item.user
        holder.userImage.loadImage(user?.image ?: "")
        holder.ratingBar.rating = (user?.rate ?: "0").toFloat()

        val stringBuilder = StringBuilder()
        stringBuilder.append(user?.fullName ?: "")
        if (user?.hasCar == true && orderType == 2) {
            stringBuilder.append(" / ").append(user.carType ?: "")
            stringBuilder.append(" ").append(user.carModel ?: "")
            stringBuilder.append(" , ").append(user.carColor ?: "")
        }
        holder.username.text = stringBuilder.toString()

        holder.showDetails.visibility = View.GONE
        holder.showDetails.setOnClickListener {
            onClick.invoke(item, position, ClickType.OPEN)
        }
//        holder.rateOfferBtn.visibility = View.GONE
//        if (item.status == acceptOfferKey && isOrderFinished)
//            holder.rateOfferBtn.visibility = View.VISIBLE
//        holder.rateOfferBtn.setOnClickListener {
//            onClick.invoke(item, position, ClickType.RATE)
//        }

        holder.callOfferBtn.visibility = View.GONE
        if (item.status == acceptOfferKey && canCall && (userId != item.user?.id)) {
            holder.callOfferBtn.visibility = View.VISIBLE
            holder.callOfferBtn.setOnClickListener {
                onClick.invoke(item, position, ClickType.CALL)
            }
        }

        holder.acceptBtn.visibility = View.GONE
        holder.rejectBtn.visibility = View.GONE
        if (isRequest) {
            holder.acceptBtn.visibility = View.VISIBLE
            holder.acceptBtn.setOnClickListener {
                onClick.invoke(item, position, ClickType.ACCEPT)
            }
            holder.rejectBtn.visibility = View.VISIBLE
            holder.rejectBtn.setOnClickListener {
                onClick.invoke(item, position, ClickType.REJECT)
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var showDetails: TextView

        //        var rateOfferBtn: TextView
        var callOfferBtn: TextView
        var acceptBtn: TextView
        var rejectBtn: TextView
        var username: TextView
        var userImage: ImageView
        var ratingBar: ScaleRatingBar


        init {
            acceptBtn = itemView.findViewById(R.id.acceptBtn)
            rejectBtn = itemView.findViewById(R.id.rejectBtn)
            callOfferBtn = itemView.findViewById(R.id.callOfferBtn)
//            rateOfferBtn = itemView.findViewById(R.id.rateOfferBtn)
            showDetails = itemView.findViewById(R.id.showDetails)
            username = itemView.findViewById(R.id.username)
            userImage = itemView.findViewById(R.id.userImage)
            ratingBar = itemView.findViewById(R.id.ratingBar)
        }
    }

    enum class ClickType {
        OPEN, RATE, CALL, ACCEPT, REJECT
    }
}