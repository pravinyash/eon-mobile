package `in`.bitspilani.eon.event_organiser.ui.adapter

import `in`.bitspilani.eon.databinding.NotificationItemRowBinding
import `in`.bitspilani.eon.event_organiser.models.Notification
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.notification_item_row.view.*

class NotificationAdapter(private val notificationList : List<Notification>,var onClearNotification: (position:Notification) -> Unit) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = NotificationItemRowBinding.inflate(inflater,parent,false)
        return NotificationViewHolder(binding)
    }

    override fun getItemCount(): Int = notificationList.size

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) = holder.bind(notificationList[position])

    inner class NotificationViewHolder(private val binding: NotificationItemRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Notification) {

            binding.notofication = item

            itemView.btn_close.setOnClickListener {
                onClearNotification(item)
            }
            binding.executePendingBindings()
        }


    }

}
