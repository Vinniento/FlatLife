package fh.wfp2.flatlife.ui.fragments.chores

import android.app.DatePickerDialog
import android.graphics.Canvas
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import fh.wfp2.flatlife.R
import fh.wfp2.flatlife.data.room.entities.Chore
import fh.wfp2.flatlife.databinding.ChoreFragmentBinding
import fh.wfp2.flatlife.other.Status
import fh.wfp2.flatlife.ui.adapters.ChoreAdapter
import fh.wfp2.flatlife.ui.adapters.OnItemClickListener
import fh.wfp2.flatlife.ui.fragments.BaseFragment
import fh.wfp2.flatlife.ui.viewmodels.chores.ChoreViewModel
import kotlinx.android.synthetic.main.add_chore_dialog.*
import kotlinx.coroutines.flow.collect
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.N)
class ChoreFragment : BaseFragment(R.layout.chore_fragment), OnItemClickListener<Chore> {
    private lateinit var _datePickerDialog: DatePickerDialog
    var day = 0
    var month: Int = 0
    var year: Int = 0
    private lateinit var binding: ChoreFragmentBinding
    private lateinit var viewModel: ChoreViewModel
    private lateinit var usersSpinner: Spinner
    private lateinit var creditSpinner: Spinner

    private val swipingItem = MutableLiveData(false)
    private val choreAdapter: ChoreAdapter = ChoreAdapter(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ChoreFragmentBinding.bind(view)
        viewModel = ViewModelProvider(this).get(ChoreViewModel::class.java)


        binding.apply {
            bChores.isActivated = true
            rvChore.apply {
                adapter = choreAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                ItemTouchHelper(SwipeToDelete(choreAdapter)).attachToRecyclerView(
                    this
                )
            }

            addChore.setOnClickListener {
                showAddChoreDialog(null)
            }

            bStatistics.setOnClickListener {

                findNavController().navigate(ChoreFragmentDirections.actionChoreFragmentToChoreStatisticsFragment())
            }
        }

        subscribeToChoreEvents()
        setupSwipeRefreshLayout()
        subscribeToObservers()

    }

    private fun subscribeToObservers() {
        viewModel.allItems.observe(viewLifecycleOwner, {
            it?.let { event ->
                val result = event.peekContent()
                when (result.status) {
                    Status.SUCCESS -> {
                        choreAdapter.choreList = result.data!!
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                    Status.ERROR -> {
                        event.getContentIfNotHandled()?.let { errorResource ->
                            errorResource.message?.let { message ->
                                showSnackbar(message)
                            }
                        }
                        result.data?.let { items ->
                            choreAdapter.choreList = items
                        }
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                    Status.LOADING -> {
                        result.data?.let { items ->
                            choreAdapter.choreList = items
                        }
                        binding.swipeRefreshLayout.isRefreshing = true
                    }
                }
            }
        })
        swipingItem.observe(viewLifecycleOwner, {
            binding.swipeRefreshLayout.isEnabled = !it
        })

    }
    private fun subscribeToChoreEvents() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.choreViewModelEvent.collect { event ->
                when (event) {
                    is ChoreViewModel.ChoreViewModelEvents.NavigateToEditChoreScreen -> {
                        showAddChoreDialog(event.chore)
                    }
                    is ChoreViewModel.ChoreViewModelEvents.ShowUndoDeleteChoreMessage -> {
                        Snackbar.make(
                            requireView(),
                            "Activity deleted",
                            Snackbar.LENGTH_SHORT
                        )
                            .setAction("Undo") {
                                viewModel.undoDeleteClick(event.chore)
                            }
                            .show()
                    }
                }
            }
        }
    }

    private fun showAddChoreDialog(chore: Chore?) {
        val dialog = MaterialDialog(requireContext())
            .noAutoDismiss()
            .customView(R.layout.add_chore_dialog)

        val users = listOf("Vince", "Frank", "Berna")
        val creditOptions = listOf(1, 2, 3, 4)

        usersSpinner = dialog.sp_assign_to
        usersSpinner.adapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                users
            )
        creditSpinner = dialog.sp_effort
        creditSpinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            creditOptions
        )

        //if passed fill in fields
        chore?.let {
            dialog.et_chore_name.setText(it.name)
            dialog.sp_effort.setSelection(creditOptions.indexOf(it.effort))
            dialog.sp_assign_to.setSelection(users.indexOf(it.assignedTo))
            dialog.tv_due_by.text = it.dueBy
        }


        dialog.b_addChore.setOnClickListener {
            val assignedTo: String =
                dialog.sp_assign_to.selectedItem as String //todo nachher vl als user direkt?
            val effort: Int = dialog.sp_effort.selectedItem as Int
            val choreName: String = dialog.et_chore_name.text.toString()
            val dueBy: String = dialog.tv_due_by.text.toString()
            val id = chore?.id ?: 0

            viewModel.addChore(
                Chore(
                    id = id,
                    name = choreName,
                    dueBy = dueBy,
                    assignedTo = assignedTo,
                    effort = effort
                )
            )
            Timber.i("name: $choreName  - dueBy: $dueBy")
            dialog.dismiss()
        }
        dialog.b_cancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.tv_due_by.setOnClickListener {
            showDatePickerDialog()
            viewModel.choreDate.observe(viewLifecycleOwner, {
                it?.let {

                    dialog.tv_due_by.text = it
                }
            })

        }


        dialog.show()
    }


    private fun setupSwipeRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.syncAllItems()
        }
    }

    override fun onItemClick(instance: Chore) {
        viewModel.onItemClick(instance)
    }

    override fun onCheckBoxClick(instance: Chore, isChecked: Boolean) {
        viewModel.onCheckBoxClicked(instance, isChecked)
    }

    private fun showDatePickerDialog() {
        val currentDate = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd MMM, YYYY", Locale.GERMAN)

        _datePickerDialog =
            DatePickerDialog(
                requireActivity(), { _, yearDialog, monthOfYear, dayOfMonth ->
                    //TODO date formaten auslagern in helper class function?
                    Timber.i("datePicker opened")
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(Calendar.YEAR, yearDialog)
                    selectedDate.set(Calendar.MONTH, monthOfYear)
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    val date = dateFormat.format(selectedDate.time)

                    day = dayOfMonth
                    year = yearDialog
                    month = monthOfYear + 1
                    viewModel.choreDate.value = date
                    Timber.i("Date: $date")

                },
                currentDate.get(Calendar.YEAR),
                currentDate.get(Calendar.MONTH),
                currentDate.get(Calendar.DAY_OF_MONTH)
            )
        _datePickerDialog.show()
    }

    inner class SwipeToDelete(var adapter: ChoreAdapter) :
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
            viewModel.onSwipedRight(adapter.choreList[position])
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                swipingItem.postValue(isCurrentlyActive)
            }
        }
    }
}