package fh.wfp2.flatlife.ui.views

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import fh.wfp2.flatlife.R
import fh.wfp2.flatlife.databinding.TasksFragmentBinding
import fh.wfp2.flatlife.ui.viewmodels.TasksViewModel
import kotlinx.android.synthetic.main.tasks_fragment.*
import timber.log.Timber


class TasksFragment : Fragment() {

    companion object {
        fun newInstance() = TasksFragment()
    }

    private lateinit var _viewModel: TasksViewModel
    private lateinit var _binding: TasksFragmentBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(
            inflater,
            R.layout.tasks_fragment, container, false
        )
        _viewModel = ViewModelProvider(this).get(TasksViewModel::class.java)

        //onClickListener here
        _binding.addTask.setOnClickListener {
            enterTaskPopup()
        }
        return _binding.root
    }

    fun enterTaskPopup() {
        val builder = AlertDialog.Builder(activity)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.add_task_popup, null)
        val editText = dialogLayout.findViewById<EditText>(R.id.taskNameEdittext)
        with(builder) {
            setTitle("Enter Task")
            setPositiveButton("Add") { dialog, which ->
                tasks.text = tasks.text.toString() + "\n" + editText.text.toString()
            }
            setNegativeButton("Cancel") { dialog, which -> Timber.i("Task creation cancelled") }
            setView(dialogLayout)
            show()
        }
        _viewModel
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
