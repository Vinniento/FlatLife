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
import fh.wfp2.flatlife.data.room.Task
import fh.wfp2.flatlife.databinding.TasksFragmentBinding
import fh.wfp2.flatlife.ui.adapters.TaskAdapter
import fh.wfp2.flatlife.ui.viewmodels.TaskViewModelFactory
import fh.wfp2.flatlife.ui.viewmodels.TasksViewModel
import timber.log.Timber
import kotlin.random.Random


class TasksFragment : Fragment(R.layout.tasks_fragment) {

    private lateinit var viewModel: TasksViewModel
    private lateinit var viewModelFactory: TaskViewModelFactory
    private lateinit var binding: TasksFragmentBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = TasksFragmentBinding.bind(view)


        val application = requireNotNull(this.activity).application
        viewModelFactory = TaskViewModelFactory(application)

        viewModel = ViewModelProvider(this, viewModelFactory).get(TasksViewModel::class.java)

        //onClickListener here


        //Recyclerview
        val adapter = TaskAdapter()
        val recyclerView = binding.tasksListRecyclerview
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        viewModel.allTasks.observe(viewLifecycleOwner, Observer { task ->
            task?.let {
                adapter.taskList = task
            }
        })

        //binding.taskViewModel = viewModel
        Timber.i("ViewModel created and added to binding")
        binding.tasksListRecyclerview.layoutManager = LinearLayoutManager(context)

        viewModel.lastTask.observe(viewLifecycleOwner, Observer {
            binding.tasksListRecyclerview.adapter = TaskAdapter()
        })

        binding.addTask.setOnClickListener {
            viewModel.insert(
                Task(
                    Random.nextLong(),
                    name = binding.tvTaskName.text.toString(),
                    createdAt = "today",
                    dueBy = "tomorrow ",
                    createdBy = "Blub"
                )

            )
            // viewModel.getAllTasks()
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
