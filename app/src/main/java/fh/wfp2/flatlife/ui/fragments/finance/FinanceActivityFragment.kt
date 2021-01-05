package fh.wfp2.flatlife.ui.fragments.finance

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import fh.wfp2.flatlife.R
import fh.wfp2.flatlife.data.room.entities.FinanceActivity
import fh.wfp2.flatlife.databinding.FinanceActivityFragmentBinding
import fh.wfp2.flatlife.ui.adapters.FinanceActivityAdapter
import fh.wfp2.flatlife.ui.viewmodels.finance.FinanceActivityViewModel
import fh.wfp2.flatlife.ui.viewmodels.finance.FinanceActivityViewModelFactory
import kotlinx.coroutines.flow.collect

class FinanceActivityFragment : Fragment(R.layout.finance_activity_fragment) {

    private lateinit var viewModel: FinanceActivityViewModel
    private lateinit var viewModelFactory: FinanceActivityViewModelFactory
    private lateinit var binding: FinanceActivityFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FinanceActivityFragmentBinding.bind(view)
        val application = requireNotNull(this.activity).application
        viewModelFactory = FinanceActivityViewModelFactory(application)
        viewModel =
            ViewModelProvider(this, viewModelFactory).get(FinanceActivityViewModel::class.java)
        val financeActivityAdapter =
            FinanceActivityAdapter { activity -> activityClicked(activity) }


        binding.apply {

            financeActivityRecyclerview.apply {
                adapter = financeActivityAdapter
                setHasFixedSize(true)
            }

            addActivity.setOnClickListener {
                viewModel.onAddActivityClick()
            }
        }

        subscribeUI(financeActivityAdapter)
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.financeCategoryEvents.collect { event ->
                when (event) {
                    is FinanceActivityViewModel.FinanceActivityEvents.NavigateToAddExpenseActivityScreen -> {
                        val action =
                            FinanceActivityFragmentDirections.actionFinanceActivityFragment2ToFinanceCategoryFragment()
                        findNavController().navigate(action)
                    }
                }
            }
        }

    }

    private fun activityClicked(financeActivity: FinanceActivity) {
        viewModel.onActivityClicked(financeActivity)
    }

    private fun subscribeUI(adapter: FinanceActivityAdapter) {
        viewModel.allItems.observe(viewLifecycleOwner, {
            it?.let {
                adapter.activityList = it

            }
        })
    }
}