package fh.wfp2.flatlife.ui.fragments.finance

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import fh.wfp2.flatlife.R
import fh.wfp2.flatlife.data.room.entities.FinanceActivity
import fh.wfp2.flatlife.databinding.AddExpenseFragmentBinding
import fh.wfp2.flatlife.ui.fragments.BaseFragment
import fh.wfp2.flatlife.ui.viewmodels.finance.AddExpenseViewModel
import fh.wfp2.flatlife.util.getItemPositionByName
import fh.wfp2.flatlife.util.hideKeyboard
import kotlinx.coroutines.flow.collect

class AddExpenseFragment : BaseFragment(R.layout.add_expense_fragment) {

    private lateinit var viewModel: AddExpenseViewModel
    private lateinit var binding: AddExpenseFragmentBinding
    private val args: AddExpenseFragmentArgs by navArgs()
    private lateinit var spinnerOptions: Spinner
    private lateinit var spinnerCategoriesList: List<String>
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = AddExpenseFragmentBinding.bind(view)
        viewModel = ViewModelProvider(this).get(AddExpenseViewModel::class.java)

        val categoryArg = args.expenseCategory
        val financeActivityArg = args.financeActivity

        viewModel.allCategories.observe(viewLifecycleOwner, { categoriesList ->

            categoriesList?.let { categories ->
                spinnerCategoriesList = categories
                spinnerOptions = binding.spCategories
                spinnerOptions.adapter = ArrayAdapter(
                    requireContext(), android.R.layout.simple_dropdown_item_1line,
                    spinnerCategoriesList
                )

                binding.apply {
                    financeActivityArg?.let { activity ->
                        spinnerOptions.setSelection(
                            spinnerCategoriesList.indexOf(activity.categoryName)
                        )
                        etAmount.setText(activity.price)
                        etDescription.setText(activity.description)

                        bSaveExpense.setOnClickListener {
                            viewModel.onUpdateExpenseClick(
                                FinanceActivity(
                                    activity.activityId,
                                    etDescription.text.toString(),
                                    spinnerOptions.selectedItem.toString(),
                                    etAmount.text.toString()//Todo schöner machen
                                )
                            )
                            hideKeyboard()
                        }
                    }

                    if (args.financeActivity == null) {
                        categoryArg?.let { ec ->
                            spinnerOptions.setSelection(
                                getPositionOfCategory(
                                    spinnerCategoriesList,
                                    ec.categoryName
                                )
                            )
                            bSaveExpense.setOnClickListener {
                                viewModel.onSaveExpenseClick(
                                    FinanceActivity(
                                        0,
                                        etDescription.text.toString(),
                                        spinnerOptions.selectedItem.toString(),
                                        etAmount.text.toString()//Todo schöner machen
                                    )
                                )
                                hideKeyboard()
                            }
                        }
                    }
                }
            }
        })
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addExpenseEvents.collect { event ->
                when (event) {
                    is AddExpenseViewModel.AddExpenseEvents.NavigateToFinanceScreen -> {
                        val action =
                            AddExpenseFragmentDirections.actionAddExpenseFragmentToFinanceActivityFragment2()
                        findNavController().navigate(action)
                    }
                }
            }
        }
    }

    fun getPositionOfCategory(list: List<String>, categoryName: String): Int {
        list.forEachIndexed { index, it ->
            if (it == categoryName)
                return index
        }
        return 0
    }
}