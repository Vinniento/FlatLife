package fh.wfp2.flatlife.ui.fragments.finance

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import fh.wfp2.flatlife.R
import fh.wfp2.flatlife.data.room.entities.ExpenseCategory
import fh.wfp2.flatlife.databinding.AddExpenseCategoryDialogBinding
import fh.wfp2.flatlife.databinding.FinanceCategoryFragmentBinding
import fh.wfp2.flatlife.other.Status
import fh.wfp2.flatlife.ui.adapters.ExpenseCategoryAdapter
import fh.wfp2.flatlife.ui.fragments.BaseFragment
import fh.wfp2.flatlife.ui.viewmodels.finance.FinanceCategoryViewModel
import kotlinx.android.synthetic.main.add_expense_category_dialog.*
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class FinanceCategoryFragment : BaseFragment(R.layout.finance_category_fragment) {

    private val viewModel: FinanceCategoryViewModel by viewModels()
    private lateinit var binding: FinanceCategoryFragmentBinding
    private lateinit var dialogBinding: AddExpenseCategoryDialogBinding
    private val categoryAdapter =
        ExpenseCategoryAdapter { expenseCategory -> categoryClicked(expenseCategory) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FinanceCategoryFragmentBinding.bind(view)

        //TODO onclick listener genau verstehen!!

        setupRecyclerViewAdapter()
        subscribeToObservers()
        setupOnClickListeners()
        setupEventChannelListeners()
    }

    private fun setupEventChannelListeners() {
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

    private fun setupRecyclerViewAdapter() {
        binding.apply {
            financeCategoryRecyclerview.apply {
                adapter = categoryAdapter
                // layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }
    }

    private fun categoryClicked(expenseCategory: ExpenseCategory) {
        viewModel.onCategoryClicked(expenseCategory)
    }

    private fun setupOnClickListeners() {
        binding.apply {


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
    }

    private fun subscribeToObservers() {
        viewModel.allItems.observe(viewLifecycleOwner, {
            it?.let { event ->
                val result = event.peekContent()
                when (result.status) {
                    Status.SUCCESS -> {
                        categoryAdapter.categoriesList = result.data!!
                    }
                    Status.ERROR -> {
                        event.getContentIfNotHandled()?.let { errorResource ->
                            errorResource.message?.let { message ->
                                showSnackbar(message)
                            }
                        }
                        result.data?.let { items ->
                            categoryAdapter.categoriesList = items
                        }
                    }
                    Status.LOADING -> {
                        result.data?.let { items ->
                            categoryAdapter.categoriesList = items
                        }
                    }
                }
            }
        })
    }
}