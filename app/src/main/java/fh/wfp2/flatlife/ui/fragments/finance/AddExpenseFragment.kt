package fh.wfp2.flatlife.ui.fragments.finance

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import fh.wfp2.flatlife.R
import fh.wfp2.flatlife.data.room.entities.FinanceActivity
import fh.wfp2.flatlife.databinding.AddExpenseFragmentBinding
import fh.wfp2.flatlife.ui.viewmodels.finance.AddExpenseViewModel
import fh.wfp2.flatlife.util.hideKeyboard
import kotlinx.coroutines.flow.collect

class AddExpenseFragment : Fragment(R.layout.add_expense_fragment) {

    private lateinit var viewModel: AddExpenseViewModel
    private lateinit var binding: AddExpenseFragmentBinding
    private val args: AddExpenseFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = AddExpenseFragmentBinding.bind(view)
        viewModel = ViewModelProvider(this).get(AddExpenseViewModel::class.java)

        val category = args.expenseCategory
        binding.apply {

            tvCategory.text = category.categoryName

            bSaveExpense.setOnClickListener {
                viewModel.onSaveExpenseClick(
                    FinanceActivity(
                        0,
                        etDescription.text.toString(),
                        category.categoryName,
                        etAmount.text.toString()//Todo schöner machen
                    )
                )
                hideKeyboard()
            }

        }

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


}