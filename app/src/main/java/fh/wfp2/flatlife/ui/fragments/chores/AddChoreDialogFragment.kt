package fh.wfp2.flatlife.ui.fragments.chores

import android.R
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import fh.wfp2.flatlife.databinding.AddChoreDialogBinding
import fh.wfp2.flatlife.ui.fragments.BaseFragment
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class AddChoreDialogFragment : BaseFragment(fh.wfp2.flatlife.R.layout.add_chore_dialog) {
    private lateinit var option: Spinner
    private lateinit var _datePickerDialog: DatePickerDialog
    private lateinit var _timePickerDialog: TimePickerDialog
    private lateinit var binding: AddChoreDialogBinding

    var day = 0
    var month: Int = 0
    var year: Int = 0
    var hour: Int = 0
    var minute: Int = 0

    @RequiresApi(Build.VERSION_CODES.N)
    private val dateFormat = SimpleDateFormat("dd MMM, YYYY", Locale.GERMAN)

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = AddChoreDialogBinding.bind(view)

        binding.apply {
            tvDueBy.setOnClickListener {
                val currentDate = Calendar.getInstance()

                _datePickerDialog =
                    DatePickerDialog(
                        requireContext(),
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
                            tvDueBy.text = "Date: $date"

                        },
                        currentDate.get(Calendar.YEAR),
                        currentDate.get(Calendar.MONTH),
                        currentDate.get(Calendar.DAY_OF_MONTH)
                    )

                _datePickerDialog.show()
            }
        }
        val options = arrayOf("Vince", "Frank", "Berna")
        option.adapter =
            ArrayAdapter<String>(
                requireContext(),
                R.layout.simple_dropdown_item_1line,
                options
            )
    }


}