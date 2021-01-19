package fh.wfp2.flatlife.ui.fragments.chores

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import fh.wfp2.flatlife.R
import fh.wfp2.flatlife.databinding.ChoreStatisticsFragmentBinding
import fh.wfp2.flatlife.ui.viewmodels.chores.ChoreStatisticsViewModel

class ChoreStatisticsFragment : Fragment(R.layout.chore_statistics_fragment) {

    companion object {
        fun newInstance() = ChoreStatisticsFragment()
    }

    private lateinit var viewModel: ChoreStatisticsViewModel
    private lateinit var binding: ChoreStatisticsFragmentBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(ChoreStatisticsViewModel::class.java)
        binding = ChoreStatisticsFragmentBinding.bind(view)
        binding.apply {
            bStatistics.isActivated = true
            bChores.setOnClickListener {
                findNavController().navigate(ChoreStatisticsFragmentDirections.actionChoreStatisticsFragmentToChoreFragment())
            }
        }
    }
}