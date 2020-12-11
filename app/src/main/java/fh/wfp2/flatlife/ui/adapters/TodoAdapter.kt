package fh.wfp2.flatlife.ui.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import fh.wfp2.flatlife.R
import fh.wfp2.flatlife.data.room.Todo
import fh.wfp2.flatlife.databinding.TodoItemBinding
import timber.log.Timber


class TodoAdapter(private val listener: OnItemClickListener) :
    RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    var todoList = listOf<Todo>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    override fun getItemCount(): Int = todoList.size


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        // val binding = TodoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.todo_item, parent, false)
        Timber.i("viewHolder created")
        val binding = TodoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TodoViewHolder(binding)
    }

    //gets called each time a view comes into view
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val currentItem = todoList[position]

        holder.bind(currentItem)
        /* holder.apply {
                cbTodo.isChecked = currentItem.isComplete
                tvTodo.text = currentItem.name
                tvTodo.paint.isStrikeThruText = currentItem.isComplete
                ivImportant.isVisible = !currentItem.isImportant
            }*/


    }

    //only gets called when viewHolder gets first created (ViewHolder get reused!!)
    inner class TodoViewHolder(private val binding: TodoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                //wenn die todo view selbst gedrückt wird
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val todo = todoList[position]
                        listener.onItemClick(todo)
                    }
                }
                cbTodoCompleted.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) //-1 -> Wenn die view gerade ausm Sichtfeld kommt
                    {
                        val todo = todoList[position]
                        listener.onCheckBoxClick(todo, !todo.isComplete)
                    }
                }

            }
        }

        fun bind(todo: Todo) {
            binding.apply {
                cbTodoCompleted.isChecked = todo.isComplete
                tvTodoName.text = todo.name
                tvTodoName.paint.isStrikeThruText = todo.isComplete
                ivImportant.isVisible = !todo.isImportant

            }

        }
/*
        val cbTodo: CheckBox = itemView.cb_todo_completed
        val tvTodo: TextView = itemView.tv_todo_name
        val ivImportant: ImageView = itemView.iv_important
*/
    }

    interface OnItemClickListener {
        fun onItemClick(todo: Todo)
        fun onCheckBoxClick(todo: Todo, isChecked: Boolean)
    }

}
