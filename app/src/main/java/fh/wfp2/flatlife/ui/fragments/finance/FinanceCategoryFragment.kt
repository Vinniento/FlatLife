package fh.wfp2.flatlife.ui.fragments.finance

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import fh.wfp2.flatlife.R
import fh.wfp2.flatlife.data.room.entities.ExpenseCategory
import fh.wfp2.flatlife.databinding.AddExpenseCategoryDialogBinding
import fh.wfp2.flatlife.databinding.FinanceCategoryFragmentBinding
import fh.wfp2.flatlife.ui.adapters.ExpenseCategoryAdapter
import fh.wfp2.flatlife.ui.viewmodels.finance.FinanceCategoryViewModel
import kotlinx.android.synthetic.main.add_expense_category_dialog.*
import kotlinx.coroutines.flow.collect
import timber.log.Timber

class FinanceCategoryFragment : Fragment(R.layout.finance_category_fragment) {

    private lateinit var viewModel: FinanceCategoryViewModel
    private lateinit var binding: FinanceCategoryFragmentBinding
    private lateinit var dialogBinding: AddExpenseCategoryDialogBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FinanceCategoryFragmentBinding.bind(view)
        viewModel = ViewModelProvider(this).get(FinanceCategoryViewModel::class.java)

        //TODO onclick listener genau verstehen!!
        val expenseCategoryAdapter =
            ExpenseCategoryAdapter { expenseCategory -> categoryClicked(expenseCategory) }




        binding.apply {

            financeCategoryRecyclerview.apply {
                adapter = expenseCategoryAdapter
                // layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }


            subscribeUI(expenseCategoryAdapter, binding)

            bCategory.setOnClickListener {
                //dialog
                val dialogView = layoutInflater.inflate(R.layout.add_expense_category_dialog, null)
                dialogBinding = AddExpenseCategoryDialogBinding.bind(dialogView)
                val builder = AlertDialog.Builder(requireContext())
                    .setView(dialogView)
                    .setTitle("Expense Category")
                val alertDialog = builder.show()
                alertDialog.b_add.setOnClickListener {
                    alertDialog.dismiss()
                    viewModel.onAddCategoryClick(
                        ExpenseCategory(0, dialogBinding.etCategoryName.text.toString())
                    )
                    Snackbar.make(
                        requireView(),
                        dialogBinding.etCategoryName.text.toString(),
                        Snackbar.LENGTH_SHORT
                    ).show()

                }
                alertDialog.b_cancel.setOnClickListener {
                    alertDialog.dismiss()
                }
            }

        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.financeCategoryEvents.collect { event ->
                when (event) {
                    is FinanceCategoryViewModel.FinanceCategoryEvents.NavigateToAddExpenseActivityScreen -> {
                        val action =
                            FinanceCategoryFragmentDirections.actionFinanceCategoryFragmentToAddExpenseFragment(
                                event.expenseCategory
                            )
                        findNavController().navigate(action)
                    }
                }
            }
        }
    }

    private fun categoryClicked(expenseCategory: ExpenseCategory) {
        viewModel.onCategoryClicked(expenseCategory)
    }

    private fun subscribeUI(
        adapter: ExpenseCategoryAdapter,
        binding: FinanceCategoryFragmentBinding
    ) {
        viewModel.allItems.observe(viewLifecycleOwner, {
            it?.let {
                //shoppingAdapter.submitList(it)
                adapter.categoriesList = it
                it.forEach { item ->
                    Timber.i("\nItem: ${item.categoryName}")
                }
            }
        })
    }
}