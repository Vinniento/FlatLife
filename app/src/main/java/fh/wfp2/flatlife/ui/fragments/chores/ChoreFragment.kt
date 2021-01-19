package fh.wfp2.flatlife.ui.fragments.chores

import android.app.DatePickerDialog
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import fh.wfp2.flatlife.R
import fh.wfp2.flatlife.data.room.entities.Chore
import fh.wfp2.flatlife.databinding.ChoreFragmentBinding
import fh.wfp2.flatlife.ui.adapters.ChoreAdapter
import fh.wfp2.flatlife.ui.adapters.OnItemClickListener
import fh.wfp2.flatlife.ui.fragments.BaseFragment
import fh.wfp2.flatlife.ui.viewmodels.chores.ChoreViewModel
import fh.wfp2.flatlife.util.getItemPositionByName
import kotlinx.android.synthetic.main.add_chore_dialog.*
import kotlinx.coroutines.flow.collect
import timber.log.Timber
import java.util.*

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
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ChoreFragmentBinding.bind(view)
        viewModel = ViewModelProvider(this).get(ChoreViewModel::class.java)
        val choreAdapter: ChoreAdapter = ChoreAdapter(this)


        binding.apply {
            bChores.isActivated = true
            rvChore.apply {
                adapter = choreAdapter
                layoutManager = LinearLayoutManager(requireContext())

                setHasFixedSize(true)
            }
            viewModel.allChores.observe(viewLifecycleOwner, {
                it?.let {
                    choreAdapter.choreList = it
                }
            })

            addChore.setOnClickListener {
                showAddChoreDialog(null)
            }

            bStatistics.setOnClickListener {

                findNavController().navigate(ChoreFragmentDirections.actionChoreFragmentToChoreStatisticsFragment())
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.choreViewModelEvent.collect { event ->
                when (event) {
                    is ChoreViewModel.ChoreViewModelEvents.NavigateToEditChoreScreen -> {
                        showAddChoreDialog(event.chore)
                    }
                }
            }
        }
    }

    private fun showDatePickerDialog() {
        val currentDate = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd MMM, YYYY", Locale.GERMAN)

        _datePickerDialog =
            DatePickerDialog(
                requireActivity(),
                DatePickerDialog.OnDateSetListener { view, yearDialog, monthOfYear, dayOfMonth ->
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

            if(chore == null)
                viewModel.addChore(
                Chore(
                    0,
                    name = choreName,
                    dueBy = dueBy,
                    assignedTo = assignedTo,
                    effort = effort
                )
            )
            else
                viewModel.onUpdateChoreClick(chore.copy(  name = choreName,
                    dueBy = dueBy,
                    assignedTo = assignedTo,
                    effort = effort))
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

    override fun onItemClick(instance: Chore) {
        viewModel.onItemClick(instance)
    }

    override fun onCheckBoxClick(instance: Chore, isChecked: Boolean) {
        viewModel.onCheckBoxClicked(instance, isChecked)
    }
}