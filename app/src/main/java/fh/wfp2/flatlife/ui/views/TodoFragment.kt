package fh.wfp2.flatlife.ui.views

import android.app.Dialog
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
import fh.wfp2.flatlife.R
import fh.wfp2.flatlife.data.preferences.SortOrder
import fh.wfp2.flatlife.data.room.Todo
import fh.wfp2.flatlife.databinding.TodoFragmentBinding
import fh.wfp2.flatlife.ui.adapters.TodoAdapter
import fh.wfp2.flatlife.ui.viewmodels.TodoViewModel
import fh.wfp2.flatlife.ui.viewmodels.TodoViewModelFactory
import fh.wfp2.flatlife.util.onQueryTextChanged
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber


class TodoFragment : Fragment(R.layout.todo_fragment), TodoAdapter.OnItemClickListener {

    private lateinit var viewModel: TodoViewModel
    private lateinit var viewModelFactory: TodoViewModelFactory
    private lateinit var binding: TodoFragmentBinding
    private val args: TodoFragmentArgs by navArgs<TodoFragmentArgs>()

    @ExperimentalCoroutinesApi
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = TodoFragmentBinding.bind(view)


        val application = requireNotNull(this.activity).application
        viewModelFactory = TodoViewModelFactory(application)

        viewModel = ViewModelProvider(this, viewModelFactory).get(TodoViewModel::class.java)

        //onClickListener here


        //Recyclerview
        //fragment implements interface mit den listeners, also kann man hier eifnach sich selbst passen
        val todoAdapter = TodoAdapter(this)

        binding.apply {
            todoListRecyclerview.apply {
                adapter = todoAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            ItemTouchHelper(SwipeToDelete(todoAdapter)).attachToRecyclerView(binding.todoListRecyclerview)

            addTodo.setOnClickListener {
                val action =
                    TodoFragmentDirections.actionTodoFragmentToAddTodoFragment(
                        Todo(0, name = null), false
                    )
                findNavController().navigate(action)


            }


        }
        viewModel.todos.observe(viewLifecycleOwner) {
            // todoAdapter.submitList(it)
            it?.let {
                todoAdapter.todoList = it
                it.forEach { todo ->
                    Timber.i(todo.toString())

                }
            }

            Timber.i("ViewModel created")
            setHasOptionsMenu(true)
        }

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
        arguments?.let {
            var taskName: String? = null
            var isImportant: Boolean = false
            arguments?.getParcelable<Todo>("Todo")?.let { todo ->

                taskName = todo.name
                isImportant = todo.isImportant
                if (arguments?.getBoolean("update")!!)
                    viewModel.onTodoNameChanged(
                        Todo(
                            todo.id,
                            name = taskName,
                            isImportant = isImportant
                        )
                    )
                else
                    viewModel.onAddTodoClick(todo)


                taskName = null
            }
        }


    }

    override fun onItemClick(todo: Todo) {
        //viewModel.onTaskSelected(todo)
        val action = TodoFragmentDirections.actionTodoFragmentToAddTodoFragment(
            Todo(
                id = todo.id,
                name = todo.name,
                isComplete = todo.isComplete,
                isImportant = todo.isImportant
            ), true
        )
        findNavController().navigate(action)
    }

    override fun onCheckBoxClick(todo: Todo, isChecked: Boolean) {
        viewModel.onTodoCheckChanged(todo, isChecked)
    }

    @ExperimentalCoroutinesApi
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_todo, menu)
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
            R.id.action_delete_all_completed_tasks -> {
                viewModel.deleteAllCompletedTodos()
                true
            }
            else -> super.onOptionsItemSelected(item)

        }
    }

    fun enterTaskPopup(taskDialog: Dialog) {
        taskDialog.layoutInflater.inflate(R.layout.add_task_popup, null)
        taskDialog.show()
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

    inner class SwipeToDelete(var adapter: TodoAdapter) :
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
            adapter.removeItemBySwipe(position)
            viewModel.onSwipedRight(adapter.todoList[position])


        }
    }
}

