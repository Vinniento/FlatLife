package fh.wfp2.flatlife.ui.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import fh.wfp2.flatlife.R
import fh.wfp2.flatlife.data.room.entities.Task
import fh.wfp2.flatlife.databinding.TaskItemCardBinding
import timber.log.Timber


class TaskAdapter(private val listener: OnItemClickListener<Task>?) :
    ListAdapter<Task, RecyclerView.ViewHolder>(TaskDiffCallback()) {

    var taskList = listOf<Task>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = taskList.size

    private var onCheckBoxClickListener: ((Task) -> Unit)? = null

    fun setOnCheckBoxListener(listener: ((Task) -> Unit)) {
        this.onCheckBoxClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        Timber.i("viewHolder created")
        val binding =
            TaskItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    //gets called each time a view comes into view
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = taskList[position]
        (holder as TaskViewHolder).bind(currentItem)
    }

    //only gets called when viewHolder gets first created (ViewHolder get reused!!)
    inner class TaskViewHolder(private val binding: TaskItemCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                cbTodoCompleted.setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION) //-1 -> Wenn die view gerade ausm Sichtfeld kommt
                    {
                        val taskItem = taskList[adapterPosition]
                        onCheckBoxClickListener?.let {
                            //hier geändert damit es nur an einer stelle geändert werden muss -> vielleicht unschön weil es ein var sein muss?
                            it(taskItem.apply { isComplete = !isComplete })
                        }
                        //listener.onCheckBoxClick(task, !task.isComplete)
                    }
                }
                listener?.let {

                    //wenn die todo view selbst gedrückt wird
                    root.setOnClickListener {
                        if (adapterPosition != RecyclerView.NO_POSITION) {
                            val task = taskList[adapterPosition]
                            listener.onItemClick(task)
                        }
                    }
                }
            }
        }

        fun bind(task: Task) {
            binding.apply {
                cbTodoCompleted.isChecked = task.isComplete
                tvTodoName.text = task.name
                tvTodoName.paint.isStrikeThruText = task.isComplete
                ivImportant.isVisible = task.isImportant
                if (!task.isSynced) {
                    ivSynced.setImageResource(R.drawable.ic_cross)
                } else {
                    ivSynced.setImageResource(R.drawable.ic_check)

                }
            }
        }
    }
}

private class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
    override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem == newItem
    }
}