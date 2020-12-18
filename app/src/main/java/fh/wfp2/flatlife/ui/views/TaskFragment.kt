package fh.wfp2.flatlife.ui.views

import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import fh.wfp2.flatlife.R
import fh.wfp2.flatlife.data.preferences.SortOrder
import fh.wfp2.flatlife.data.room.Task
import fh.wfp2.flatlife.databinding.TaskFragmentBinding
import fh.wfp2.flatlife.ui.adapters.TaskAdapter
import fh.wfp2.flatlife.ui.viewmodels.TaskViewModel
import fh.wfp2.flatlife.ui.viewmodels.TaskViewModelFactory
import fh.wfp2.flatlife.util.onQueryTextChanged
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber


class TaskFragment : Fragment(R.layout.task_fragment), TaskAdapter.OnItemClickListener {

    private lateinit var viewModel: TaskViewModel
    private lateinit var viewModelFactory: TaskViewModelFactory
    private lateinit var binding: TaskFragmentBinding
    private val args: TaskFragmentArgs by navArgs<TaskFragmentArgs>()

    @InternalCoroutinesApi
    @ExperimentalCoroutinesApi
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = TaskFragmentBinding.bind(view)


        val application = requireNotNull(this.activity).application
        viewModelFactory = TaskViewModelFactory(application)

        viewModel = ViewModelProvider(this, viewModelFactory).get(TaskViewModel::class.java)
        Timber.i("ViewModel created")

        arguments?.let {
            var taskName: String?
            var isImportant: Boolean

            arguments?.getParcelable<Task>("Task")?.let { task ->

                taskName = task.name
                isImportant = task.isImportant
                if (arguments?.getBoolean("update")!!) {
                    viewModel.onTaskChanged(
                        Task(
                            task.id,
                            name = taskName,
                            isImportant = isImportant
                        )
                    )

                } else
                    viewModel.onAddTaskClick(task)


                taskName = null
            }
        }

        //Recyclerview
        //fragment implements interface mit den listeners, also kann man hier eifnach sich selbst passen
        val todoAdapter = TaskAdapter(this)

        binding.apply {
            taskListRecyclerview.apply {
                adapter = todoAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)

                ItemTouchHelper(SwipeToDelete(todoAdapter)).attachToRecyclerView(
                    taskListRecyclerview
                )

                addTask.setOnClickListener {
                    val action =
                        TaskFragmentDirections.actionTaskFragmentToAddTaskFragment(
                            Task(0, name = null), false
                        )
                    findNavController().navigate(action)
                }

            }
        }
        viewModel.tasks.observe(viewLifecycleOwner) {
            // todoAdapter.submitList(it)
            it?.let {
                todoAdapter.taskList = it
                it.forEach { todo ->
                    Timber.i(todo.toString())

                }
            }
        }

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
                }
            }
        }
        setHasOptionsMenu(true)

        /*  args?.let { //debugger stops here and app crashes
              if(args.taskName != null)
                  viewModel.onAddTodoClick(Todo(name = args.taskName, isImportant = args.isImportant))
          }*/

/*        val args = arguments?.let {
            AddTodoFragmentArgs.fromBundle(it)
        }
        if(args != null){
            Timber.i("Taskname: ${args.taskName}")
            Timber.i("Is Important: ${args.isImportant}")
        }**/

        /* args.Todo.name?.let {

             viewModel.onAddTodoClick(args.Todo.copy())

         }*/


    }

    override fun onItemClick(task: Task) {
        //viewModel.onTaskSelected(todo)
        val action = TaskFragmentDirections.actionTaskFragmentToAddTaskFragment(
            Task(
                id = task.id,
                name = task.name,
                isComplete = task.isComplete,
                isImportant = task.isImportant
            ), true
        )
        findNavController().navigate(action)
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

