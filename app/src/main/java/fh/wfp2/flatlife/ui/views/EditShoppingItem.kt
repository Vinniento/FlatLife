package fh.wfp2.flatlife.ui.views

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import fh.wfp2.flatlife.R
import fh.wfp2.flatlife.databinding.AddTaskFragmentBinding
import fh.wfp2.flatlife.ui.viewmodels.AddTaskFragmentViewModel
import kotlinx.coroutines.flow.collect
import timber.log.Timber

class EditShoppingItem : Fragment(R.layout.add_task_fragment) {

    private lateinit var binding: AddTaskFragmentBinding

    private val args: AddTaskFragmentArgs by navArgs()
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
                    etAddTask.setText(viewModel.task.value?.name)
                    cbImportant.isChecked = viewModel.task.value?.isImportant ?: false
                    // cbImportant.jumpDrawablesToCurrentState() =
                }
            }
        })

        binding.bAddTask.setOnClickListener {

            viewModel.onAddTaskClick(
                binding.etAddTask.text.toString(),
                binding.cbImportant.isChecked
            )
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addTasksEvent.collect { event ->
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
                            AddTaskFragmentDirections.actionAddTaskFragmentToTaskFragment()
                        findNavController().navigate(action)
                    }
                }
            }
        }

    }
}