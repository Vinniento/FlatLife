package fh.wfp2.flatlife.ui.views

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import fh.wfp2.flatlife.R
import fh.wfp2.flatlife.databinding.AddTaskFragmentBinding

class AddTodoFragment : Fragment(R.layout.add_task_fragment) {

    private lateinit var binding: AddTaskFragmentBinding


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var taskName: String? = null
        var isImportant: Boolean
        binding = AddTaskFragmentBinding.bind(view)

        arguments?.let {
            arguments?.getString("taskName")?.let { taskname ->
                taskName = taskname
            }
            arguments?.getBoolean("isImportant")?.let {
                isImportant = it

                if (taskName != null) {
                    binding.cbImportant.isChecked = isImportant
                    binding.etAddTodo.setText(taskName)
                }

            }

        }

        binding.bAddTodo.setOnClickListener {
            if (binding.etAddTodo.text.isNotEmpty()) {
                val taskName = binding.etAddTodo.text.toString()
                val isImportant = binding.cbImportant.isChecked
                val action = AddTodoFragmentDirections.actionAddTodoFragmentToTodoFragment(
                    taskName,
                    isImportant
                )
                findNavController().navigate(action)

            } else {
                Snackbar.make(it, "The task field can't be empty", Snackbar.LENGTH_SHORT).show()
            }
        }

    }
}