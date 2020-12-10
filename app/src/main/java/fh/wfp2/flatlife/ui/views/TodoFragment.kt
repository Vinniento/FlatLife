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
import androidx.recyclerview.widget.LinearLayoutManager
import fh.wfp2.flatlife.R
import fh.wfp2.flatlife.data.room.Todo
import fh.wfp2.flatlife.databinding.TodoFragmentBinding
import fh.wfp2.flatlife.ui.adapters.TodoAdapter
import fh.wfp2.flatlife.ui.viewmodels.SortOrder
import fh.wfp2.flatlife.ui.viewmodels.TodoViewModel
import fh.wfp2.flatlife.ui.viewmodels.TodoViewModelFactory
import fh.wfp2.flatlife.util.onQueryTextChanged
import kotlinx.coroutines.ExperimentalCoroutinesApi
import timber.log.Timber
import kotlin.random.Random


class TodoFragment : Fragment(R.layout.todo_fragment) {

    private lateinit var viewModel: TodoViewModel
    private lateinit var viewModelFactory: TodoViewModelFactory
    private lateinit var binding: TodoFragmentBinding

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
        val todoAdapter = TodoAdapter()

        binding.apply {
            todoListRecyclerview.apply {
                adapter = todoAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }


            addTodo.setOnClickListener {
                viewModel.insert(
                    Todo(
                        name = binding.etNewTodo.text.toString(),
                        createdBy = "Blub",
                        important = Random.nextBoolean()
                    )
                )
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
    }

    @ExperimentalCoroutinesApi
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort_by_name -> {
                viewModel.sortOrder.value = SortOrder.BY_NAME
                true
            }
            R.id.action_sort_by_date_created -> {
                viewModel.sortOrder.value = SortOrder.BY_DATE
                true
            }
            R.id.action_hide_completed_tasks -> {
                item.isChecked = !item.isChecked
                viewModel.hideCompleted.value = item.isChecked
                true
            }
            R.id.action_delete_all_completed_tasks -> {
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


}
