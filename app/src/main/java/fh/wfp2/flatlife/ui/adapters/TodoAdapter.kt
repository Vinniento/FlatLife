package fh.wfp2.flatlife.ui.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import fh.wfp2.flatlife.R
import fh.wfp2.flatlife.data.room.Todo
import kotlinx.android.synthetic.main.todo_item.view.*
import timber.log.Timber

class TodoAdapter :
    RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    var todoList = listOf<Todo>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = todoList.size


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.todo_item, parent, false)
        Timber.i("viewHolder created")
        return TodoViewHolder(itemView)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val currentItem = todoList[position]
        holder.todo.text = currentItem.name
        holder.todo.isChecked = currentItem.isComplete

    }


    class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val todo: CheckBox = itemView.cb_todo
    }

}
