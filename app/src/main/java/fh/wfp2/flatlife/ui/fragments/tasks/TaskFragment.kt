package fh.wfp2.flatlife.ui.fragments.tasks

import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import fh.wfp2.flatlife.R
import fh.wfp2.flatlife.data.preferences.SortOrder
import fh.wfp2.flatlife.data.room.entities.Task
import fh.wfp2.flatlife.databinding.TaskFragmentBinding
import fh.wfp2.flatlife.other.Status
import fh.wfp2.flatlife.ui.adapters.OnItemClickListener
import fh.wfp2.flatlife.ui.adapters.TaskAdapter
import fh.wfp2.flatlife.ui.fragments.BaseFragment
import fh.wfp2.flatlife.ui.viewmodels.TaskViewModel
import fh.wfp2.flatlife.util.hideKeyboard
import fh.wfp2.flatlife.util.onQueryTextChanged
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class TaskFragment : BaseFragment(R.layout.task_fragment), OnItemClickListener<Task> {

    private val viewModel: TaskViewModel by viewModels()
    private lateinit var binding: TaskFragmentBinding
    val todoAdapter = TaskAdapter(this)

    @InternalCoroutinesApi
    @ExperimentalCoroutinesApi
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideKeyboard()
        binding = TaskFragmentBinding.bind(view)

        Timber.i("ViewModel created")

        //Recyclerview
        //fragment implements interface mit den listeners, also kann man hier eifnach sich selbst passen

        binding.apply {
            taskListRecyclerview.apply {
                adapter = todoAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)

                ItemTouchHelper(SwipeToDelete(todoAdapter)).attachToRecyclerView(
                    taskListRecyclerview
                )
            }
            addTask.setOnClickListener {
                viewModel.onAddNewTaskClick()
            }
            constraintLayout.setOnClickListener {
                viewModel.syncAllTasks()
            }
        }

        subscribeToObservers()

        setHasOptionsMenu(true)

    }

    private fun subscribeToObservers() {
        //coroutine will be canceled when onStop is called. Will only listen for events when fragment is displayed
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.tasksEvent.collect { event ->
                when (event) {
                    is TaskViewModel.TaskEvent.ShowUndoDeleteTaskMessage -> {
                        Snackbar.make(requireView(), "Task deleted", Snackbar.LENGTH_LONG)
                            .setAction("UNDO") {
                                viewModel.onUndoDeleteClick(event.task)
                            }.show()
                    }
                    is TaskViewModel.TaskEvent.NavigateToAddTaskScreen -> {
                        val action = TaskFragmentDirections.actionTaskFragmentToAddTaskFragment()
                        findNavController().navigate(action)
                    }
                    is TaskViewModel.TaskEvent.NavigateToEditTaskScreen -> {
                        val action =
                            TaskFragmentDirections.actionTaskFragmentToAddTaskFragment(event.task)
                        findNavController().navigate(action)
                    }
                }
            }
        }


        /*viewModel.tasks.observe(viewLifecycleOwner) {
            // todoAdapter.submitList(it)
            it?.let {
                todoAdapter.taskList = it
            }
        }*/


        viewModel.allTasks.observe(viewLifecycleOwner, {
            it?.let { event ->
                val result = event.peekContent()

                when (result.status) {
                    Status.SUCCESS -> {
                        todoAdapter.taskList = result.data!!
                    }
                    Status.LOADING -> {
                        result.data?.let { tasks ->
                            todoAdapter.taskList = tasks
                        }
                    }
                    Status.ERROR -> {
                        event.getContentIfNotHandled()?.let { errorResource ->
                            errorResource.message?.let { message ->
                                showSnackbar(message)
                            }
                        }
                        result.data?.let { tasks ->
                            todoAdapter.taskList = tasks
                        }
                    }
                }
            }
        })

    }

    override fun onItemClick(task: Task) {
        viewModel.onTaskSelected(task)
    }

    override fun onCheckBoxClick(task: Task, isChecked: Boolean) {
        viewModel.onTaskCheckChanged(task, isChecked)
    }

    @ExperimentalCoroutinesApi
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_task, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.onQueryTextChanged {
            //update search query
            viewModel.searchQuery.value = it
        }

        viewLifecycleOwner.lifecycleScope.launch {
            menu.apply {
                findItem(R.id.action_hide_completed_tasks).isChecked =
                    viewModel.preferencesFlow.first().hideCompleted

            }
        }
    }

    @ExperimentalCoroutinesApi
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort_by_name -> {
                viewModel.onSortOrderSelected(SortOrder.BY_NAME)
                true
            }
            R.id.action_sort_by_date_created -> {
                viewModel.onSortOrderSelected(SortOrder.BY_DATE)
                true
            }
            R.id.action_hide_completed_tasks -> {
                item.isChecked = !item.isChecked
                viewModel.onHideCompletedClick(item.isChecked)
                true
            }
            R.id.action_delete_all_bought_items -> {
                viewModel.deleteAllCompletedTasks()
                true
            }
            else -> super.onOptionsItemSelected(item)

        }
    }


    override fun onResume() {
        super.onResume()

        Timber.i("onResume called")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.i("onCreate called")
    }

    override fun onStart() {
        super.onStart()
        Timber.i("onStart called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.i("onDestroy called")
    }

    override fun onStop() {
        super.onStop()
        Timber.i("onStopCalled")
    }

    inner class SwipeToDelete(var adapter: TaskAdapter) :
        ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition
            viewModel.onSwipedRight(adapter.taskList[position])


        }
    }
}

