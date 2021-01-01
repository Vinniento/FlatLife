package fh.wfp2.flatlife.ui.fragments.finance

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import fh.wfp2.flatlife.R
import fh.wfp2.flatlife.databinding.FinanceActivityFragmentBinding
import fh.wfp2.flatlife.ui.viewmodels.finance.FinanceViewModel

class FinanceActivityFragment : Fragment(R.layout.finance_activity_fragment) {

    private lateinit var viewModel: FinanceViewModel

    private lateinit var binding: FinanceActivityFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FinanceActivityFragmentBinding.bind(view)
        binding.addActivity.setOnClickListener {
            val action =
                FinanceActivityFragmentDirections.actionFinanceActivityFragment2ToFinanceCategoryFragment()
            findNavController().navigate(action)
        }

    }
}