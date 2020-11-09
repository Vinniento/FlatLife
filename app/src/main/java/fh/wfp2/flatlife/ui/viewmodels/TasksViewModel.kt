package fh.wfp2.flatlife.ui.viewmodels

import androidx.lifecycle.ViewModel

class TasksViewModel : ViewModel() {

    val tasks: MutableList<String> = mutableListOf()

    fun addTask(taskName: String) {
        tasks.add(taskName)
    }


}
