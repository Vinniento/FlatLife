package fh.wfp2.flatlife.ui.fragments.tasks

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import fh.wfp2.flatlife.other.Status
import dagger.hilt.android.AndroidEntryPoint
import fh.wfp2.flatlife.R
import fh.wfp2.flatlife.data.room.entities.Task
import fh.wfp2.flatlife.databinding.AddTaskFragmentBinding
import fh.wfp2.flatlife.ui.fragments.BaseFragment
import fh.wfp2.flatlife.ui.viewmodels.tasks.AddEditTaskFragmentViewModel
import fh.wfp2.flatlife.util.hideKeyboard
import kotlinx.coroutines.flow.collect
import timber.log.Timber
import kotlin.random.Random

@AndroidEntryPoint
class AddEditTaskFragment : BaseFragment(R.layout.add_task_fragment) {

    private lateinit var binding: AddTaskFragmentBinding

    private val args: AddEditTaskFragmentArgs by navArgs()
    private val viewModel: AddEditTaskFragmentViewModel by viewModels()
    private var curTask: Task? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = AddTaskFragmentBinding.bind(view)

        args.task?.let {
            viewModel.onArgumentsPassed(it)
            subscribeToObservers()
        }

        binding.bAddTask.setOnClickListener {
            val name = binding.etTaskName.text.toString()
            val importance = binding.cbIsImportant.isChecked
            val date = System.currentTimeMillis()

            val id = curTask?.id ?: Random.nextLong()
            val task = Task(id = id, name = name, createdAt = date, isImportant = importance, isComplete = curTask?.isComplete ?: false )
            viewModel.onAddTaskClick(task)
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addTasksEvent.collect { event ->
                hideKeyboard()
                when (event) {
                    is AddEditTaskFragmentViewModel.AddTaskEvent.ShowIncompleteTaskMessage -> {
                        showSnackbar("Task name field can't be left empty!")
                        Timber.i("Snackbar shown")
                    }

                    is AddEditTaskFragmentViewModel.AddTaskEvent.NavigateToTaskFragmentScreen -> {
                        val action =
                            AddEditTaskFragmentDirections.actionAddTaskFragmentToTaskFragment()
                        findNavController().navigate(action)
                    }
                }
            }
        }
    }

    private fun subscribeToObservers() {
        viewModel.task.observe(viewLifecycleOwner, {
            it?.getContentIfNotHandled()?.let { result ->
                when (result.status) {
                    Status.SUCCESS -> {
                        val task = result.data!!
                        curTask = task
                        binding.etTaskName.setText(task.name)
                        binding.cbIsImportant.isChecked = task.isImportant
                    }
                    Status.ERROR -> {
                        showSnackbar(result.message ?: "Something went wrong...")
                    }
                    Status.LOADING -> {
                        //hier wird nie geladen
                    }
                }
            }
        })
    }

    private fun saveTask() {

    }
}