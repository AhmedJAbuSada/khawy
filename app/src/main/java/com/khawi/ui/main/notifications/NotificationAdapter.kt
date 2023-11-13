package com.khawi.ui.main.notifications

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
import com.khawi.base.formatDateTime
import com.khawi.model.Notification


class NotificationAdapter(
    private val ctx: Context,
    private val onClick: (item: Notification, position: Int) -> Unit
) : ListAdapter<Notification, NotificationAdapter.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Notification>() {
            override fun areItemsTheSame(oldItem: Notification, newItem: Notification) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Notification, newItem: Notification) =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(ctx).inflate(R.layout.row_notification, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = currentList[position]

        holder.normalNotificationContainer.visibility = View.GONE
        holder.actionNotificationContainer.visibility = View.GONE
        when (item.type) {
//            1 -> {
//                holder.actionNotificationContainer.visibility = View.VISIBLE
//                holder.iconIV.background = ContextCompat.getDrawable(ctx, R.drawable.bg_green_12r)
//                holder.iconIV.setImageResource(R.drawable.accept_order_notification)
//                holder.notificationActionTitle.text = "موافقة على الانضمام"
//                holder.notificationActionContent.text = "جاسم صباح، وافق\n" +
//                        "على انضمامك لرحلته."
//                holder.notificationActionTime.text = "09.45"
//            }

            1 -> {
                holder.actionNotificationContainer.visibility = View.VISIBLE
                holder.iconIV.background = ContextCompat.getDrawable(ctx, R.drawable.bg_orange_12r)
                holder.iconIV.setImageResource(R.drawable.order_notification)
                holder.notificationTitle.text = item.title ?: ""
                holder.notificationContent.text = item.msg ?: ""
                holder.notificationTime.text = item.dtDate?.formatDateTime() ?: ""
            }

            else -> {
                holder.normalNotificationContainer.visibility = View.VISIBLE
                holder.notificationTitle.text = item.title ?: ""
                holder.notificationContent.text = item.msg ?: ""
                holder.notificationTime.text = item.dtDate?.formatDateTime() ?: ""
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
        var normalNotificationContainer: View
        var notificationTitle: TextView
        var notificationContent: TextView
        var notificationTime: TextView

        var actionNotificationContainer: View
        var iconIV: ImageView
        var notificationActionTitle: TextView
        var notificationActionContent: TextView
        var notificationActionTime: TextView


        init {
            normalNotificationContainer = itemView.findViewById(R.id.normalNotificationContainer)
            notificationTitle = itemView.findViewById(R.id.notificationTitle)
            notificationContent = itemView.findViewById(R.id.notificationContent)
            notificationTime = itemView.findViewById(R.id.notificationTime)

            actionNotificationContainer = itemView.findViewById(R.id.actionNotificationContainer)
            iconIV = itemView.findViewById(R.id.iconIV)
            notificationActionTitle = itemView.findViewById(R.id.notificationActionTitle)
            notificationActionContent = itemView.findViewById(R.id.notificationActionContent)
            notificationActionTime = itemView.findViewById(R.id.notificationActionTime)
        }
    }

}