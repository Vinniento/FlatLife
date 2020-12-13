package fh.wfp2.flatlife.ui.views

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import fh.wfp2.flatlife.R
import fh.wfp2.flatlife.data.room.Todo
import fh.wfp2.flatlife.databinding.AddTaskFragmentBinding

class AddTodoFragment : Fragment(R.layout.add_task_fragment) {

    private lateinit var binding: AddTaskFragmentBinding

    private val args by navArgs<AddTodoFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //   var taskName: String? = null
        //var isImportant: Boolean
        binding = AddTaskFragmentBinding.bind(view)
        val isUpdate = args.update
        var todoId: Long = 0
        var todo: Todo = Todo(0, "")
        args.Todo.name?.let {
            todo = args.Todo
            todoId = args.Todo.id
            binding.cbImportant.isChecked = args.Todo.isImportant
            binding.etAddTodo.setText(args.Todo.name)
            binding.bAddTodo.text = "Update"
        }

        /*  arguments?.let {
              arguments?.getString("taskName")?.let { taskname ->
                  taskName = taskname
              }
              arguments?.getBoolean("isImportant")?.let {
                  isImportant = it

                  if (taskName != null) {
                      binding.cbImportant.isChecked = isImportant
                      binding.etAddTodo.setText(taskName)
                      binding.bAddTodo.setText("Update")
                  }

              }
              taskName = null
          }*/

        binding.bAddTodo.setOnClickListener {
            if (binding.etAddTodo.text.isNotEmpty()) {
                val taskName = binding.etAddTodo.text.toString()
                val isImportant = binding.cbImportant.isChecked
                val action = AddTodoFragmentDirections.actionAddTodoFragmentToTodoFragment(
                    Todo(todoId, taskName, isComplete = todo.isComplete, isImportant = isImportant),
                    isUpdate
                )
                findNavController().navigate(action)

            } else {
                Snackbar.make(it, "The task field can't be empty", Snackbar.LENGTH_SHORT).show()
            }
        }

    }
}