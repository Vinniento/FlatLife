package fh.wfp2.flatlife.ui.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import fh.wfp2.flatlife.R
import fh.wfp2.flatlife.data.room.Task
import kotlinx.android.synthetic.main.task_item.view.*
import timber.log.Timber

class TaskAdapter :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    var taskList = listOf<Task>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = taskList.size


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false)
        Timber.i("viewHolder created")
        return TaskViewHolder(itemView)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentItem = taskList[position]
        holder.taskName.text = currentItem.name
        holder.dueBy.text = currentItem.dueBy

    }


    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val taskName: TextView = itemView.task_name_textview
        val dueBy: TextView = itemView.task_dueBy_textview
    }

}
