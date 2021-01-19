package fh.wfp2.flatlife.ui.fragments.finance

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import fh.wfp2.flatlife.R
import fh.wfp2.flatlife.databinding.FinanceBalanceFragmentBinding
import fh.wfp2.flatlife.ui.fragments.BaseFragment
import fh.wfp2.flatlife.ui.viewmodels.finance.FinanceBalanceViewModel

class FinanceBalanceFragment : BaseFragment(R.layout.finance_balance_fragment) {

    private val viewModel: FinanceBalanceViewModel by viewModels()
    private lateinit var binding: FinanceBalanceFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FinanceBalanceFragmentBinding.bind(view)

        binding.apply {

            bBalance.isActivated = true
            bActivity.setOnClickListener {
                findNavController().navigate(FinanceBalanceFragmentDirections.actionFinanceBalanceFragmentToFinanceActivityFragment2())
            }
        }
    }
}