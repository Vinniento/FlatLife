package fh.wfp2.flatlife.ui.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import fh.wfp2.flatlife.R
import fh.wfp2.flatlife.data.room.Task
import timber.log.Timber

class ShoppingAdapter :
    RecyclerView.Adapter<ShoppingAdapter.TaskViewHolder>() {

    var shoppingList = listOf<Task>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = shoppingList.size


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.shopping_item_card, parent, false)
        Timber.i("viewHolder created")
        return TaskViewHolder(itemView)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentItem = shoppingList[position]


    }


    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}
