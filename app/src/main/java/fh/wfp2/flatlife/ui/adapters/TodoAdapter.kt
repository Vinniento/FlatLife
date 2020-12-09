package fh.wfp2.flatlife.ui.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import fh.wfp2.flatlife.data.room.Todo
import fh.wfp2.flatlife.databinding.TodoItemBinding
import timber.log.Timber

interface ItemListener {
    fun onCheckChangedListener(todo: Todo, position: Int)
}

class TodoAdapter :
    ListAdapter<Todo, TodoAdapter.TodoViewHolder>(DiffCallback()) {

    var todoList = listOf<Todo>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    private lateinit var listener: ItemListener

    fun setListener(listener: ItemListener) {
        this.listener = listener
    }

    override fun getItemCount(): Int = todoList.size


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val binding = TodoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.todo_item, parent, false)
        Timber.i("viewHolder created")
        return TodoViewHolder(binding)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)

        /*Alte version ohne binding
        holder.todo.text = currentItem.name
         holder.todo.isChecked = currentItem.isComplete
         */
        /*holder.todo.setOnCheckedChangeListener { buttonView, isChecked ->


            Timber.i("Checked status $isChecked")*/
    }


    class TodoViewHolder(private val binding: TodoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(todo: Todo) {
            binding.apply {
                cbTodo.isChecked = todo.isComplete
                tvTodoName.text = todo.name

            }

        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Todo>() {
        override fun areItemsTheSame(oldItem: Todo, newItem: Todo) =
            oldItem.todoId == newItem.todoId

        override fun areContentsTheSame(oldItem: Todo, newItem: Todo) = oldItem == newItem

    }
}
