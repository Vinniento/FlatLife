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
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import fh.wfp2.flatlife.R
import fh.wfp2.flatlife.data.room.entities.Chore
import fh.wfp2.flatlife.databinding.ChoreFragmentBinding
import fh.wfp2.flatlife.ui.adapters.ChoreAdapter
import fh.wfp2.flatlife.ui.viewmodels.chores.ChoreViewModel
import kotlinx.android.synthetic.main.add_chore_dialog.*
import timber.log.Timber
import java.util.*

@RequiresApi(Build.VERSION_CODES.N)
class ChoreFragment : Fragment(R.layout.chore_fragment) {
    private lateinit var _datePickerDialog: DatePickerDialog
    var day = 0
    var month: Int = 0
    var year: Int = 0
    private lateinit var binding: ChoreFragmentBinding
    private lateinit var viewModel: ChoreViewModel
    private lateinit var option: Spinner
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ChoreFragmentBinding.bind(view)
        viewModel = ViewModelProvider(this).get(ChoreViewModel::class.java)
        val choreAdapter: ChoreAdapter = ChoreAdapter()


        binding.apply {
            rvChores.apply {
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
                showAddChoreDialog()
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


    private fun showAddChoreDialog() {
        val dialog = MaterialDialog(requireContext())
            .noAutoDismiss()
            .customView(R.layout.add_chore_dialog)
        dialog.b_addChore.setOnClickListener {

            val choreName: String = dialog.et_chore_name.text.toString()
            val dueBy: String = dialog.tv_due_by.text.toString()
            viewModel.addChore(Chore(0, choreName, dueBy))
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
        val options = arrayOf("Vince", "Frank", "Berna")
        option = dialog.sp_assign_to
        option.adapter =
            ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                options
            )

        dialog.show()
    }
}