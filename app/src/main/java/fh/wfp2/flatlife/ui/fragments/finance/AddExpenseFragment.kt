package fh.wfp2.flatlife.ui.fragments.finance

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import fh.wfp2.flatlife.R
import fh.wfp2.flatlife.ui.viewmodels.finance.AddExpenseViewModel

class AddExpenseFragment : Fragment() {

    companion object {
        fun newInstance() = AddExpenseFragment()
    }

    private lateinit var viewModel: AddExpenseViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_expense_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AddExpenseViewModel::class.java)
        // TODO: Use the ViewModel
    }

}