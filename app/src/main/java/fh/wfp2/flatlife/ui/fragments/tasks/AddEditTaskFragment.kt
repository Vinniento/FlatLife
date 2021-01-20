package fh.wfp2.flatlife.ui.fragments.tasks

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import fh.wfp2.flatlife.R
import fh.wfp2.flatlife.data.room.entities.Task
import fh.wfp2.flatlife.databinding.AddTaskFragmentBinding
import fh.wfp2.flatlife.ui.fragments.BaseFragment
import fh.wfp2.flatlife.ui.viewmodels.tasks.AddTaskFragmentViewModel
import fh.wfp2.flatlife.util.hideKeyboard
import kotlinx.coroutines.flow.collect
import timber.log.Timber
import kotlin.random.Random

@AndroidEntryPoint
class AddEditTaskFragment : BaseFragment(R.layout.add_task_fragment) {

    private lateinit var binding: AddTaskFragmentBinding

    private val args: AddEditTaskFragmentArgs by navArgs()
    private val viewModel: AddTaskFragmentViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = AddTaskFragmentBinding.bind(view)

        args.task?.let {
            viewModel.onArgumentsPassed(it)
        }

        viewModel.task.observe(viewLifecycleOwner, {
            binding.apply {
                it?.let {
                    etTaskName.setText(viewModel.task.value?.name)
                    cbIsImportant.isChecked = viewModel.task.value?.isImportant ?: false
                    // cbImportant.jumpDrawablesToCurrentState() =
                }
            }
        })

        binding.bAddTask.setOnClickListener {

            val name = binding.etTaskName.text.toString()
            val importance = binding.cbIsImportant.isChecked
            val id = Random.nextLong()
            viewModel.onAddTaskClick(Task(id, name = name, isImportant = importance))
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {

            viewModel.addTasksEvent.collect { event ->
                hideKeyboard()
                when (event) {
                    is AddTaskFragmentViewModel.AddTaskEvent.ShowIncompleteTaskMessage -> {
                        Snackbar.make(
                            requireView(),
                            "The task field can't be empty",
                            Snackbar.LENGTH_SHORT
                        ).show()
                        Timber.i("Snackbar shown")
                    }

                    is AddTaskFragmentViewModel.AddTaskEvent.NavigateToTaskFragmentScreen -> {

                        val action =
                            AddEditTaskFragmentDirections.actionAddTaskFragmentToTaskFragment()
                        findNavController().navigate(action)
                    }
                }
            }
        }
    }
}