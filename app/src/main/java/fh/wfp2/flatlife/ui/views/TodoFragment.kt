package fh.wfp2.flatlife.ui.views

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import fh.wfp2.flatlife.R
import fh.wfp2.flatlife.data.room.Todo
import fh.wfp2.flatlife.databinding.TodoFragmentBinding
import fh.wfp2.flatlife.ui.adapters.TodoAdapter
import fh.wfp2.flatlife.ui.viewmodels.TodoViewModel
import fh.wfp2.flatlife.ui.viewmodels.TodoViewModelFactory
import timber.log.Timber
import java.time.LocalTime
import kotlin.random.Random


class TodoFragment : Fragment(R.layout.todo_fragment) {

    private lateinit var viewModel: TodoViewModel
    private lateinit var viewModelFactory: TodoViewModelFactory
    private lateinit var binding: TodoFragmentBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = TodoFragmentBinding.bind(view)


        val application = requireNotNull(this.activity).application
        viewModelFactory = TodoViewModelFactory(application)

        viewModel = ViewModelProvider(this, viewModelFactory).get(TodoViewModel::class.java)

        //onClickListener here


        //Recyclerview
        val adapter = TodoAdapter()
        val recyclerView = binding.todoListRecyclerview
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        viewModel.allTodos.observe(viewLifecycleOwner, Observer { todo ->
            todo?.let {
                adapter.todoList = todo
            }
        })

        Timber.i("ViewModel created and added to binding")
        binding.todoListRecyclerview.layoutManager = LinearLayoutManager(context)

        viewModel.latestTodo.observe(viewLifecycleOwner, Observer {
            binding.todoListRecyclerview.adapter = TodoAdapter()
        })

        binding.addTodo.setOnClickListener {
            viewModel.insert(
                Todo(
                    Random.nextLong(),
                    name = "New Todo: ${Random.nextInt().toString().take(4)}",
                    createdAt = "${LocalTime.now()}",
                    createdBy = "Blub",
                    isComplete = Random.nextBoolean()
                )
            )
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
