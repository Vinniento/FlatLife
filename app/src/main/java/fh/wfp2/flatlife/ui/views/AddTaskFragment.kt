package fh.wfp2.flatlife.ui.views

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import fh.wfp2.flatlife.R
import fh.wfp2.flatlife.data.room.Task
import fh.wfp2.flatlife.databinding.AddTaskFragmentBinding

class AddTaskFragment : Fragment(R.layout.add_task_fragment) {

    private lateinit var binding: AddTaskFragmentBinding

    private val args by navArgs<AddTaskFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //   var taskName: String? = null
        //var isImportant: Boolean
        binding = AddTaskFragmentBinding.bind(view)
        val isUpdate = args.update
        var taskId: Long = 0
        var task: Task = Task(0, "")
        args.Task.name?.let {
            task = args.Task
            taskId = args.Task.id
            binding.cbImportant.isChecked = args.Task.isImportant
            binding.etAddTask.setText(args.Task.name)
            binding.bAddTask.text = "Update"
        }

        binding.bAddTask.setOnClickListener {
            if (binding.etAddTask.text.isNotEmpty()) {
                val taskName = binding.etAddTask.text.toString()
                val isImportant = binding.cbImportant.isChecked
                val action = AddTaskFragmentDirections.actionAddTaskFragmentToTaskFragment(
                    Task(taskId, taskName, isComplete = task.isComplete, isImportant = isImportant),
                    isUpdate
                )
                findNavController().navigate(action)

            } else {
                Snackbar.make(it, "The task field can't be empty", Snackbar.LENGTH_SHORT).show()
            }
        }

    }
}