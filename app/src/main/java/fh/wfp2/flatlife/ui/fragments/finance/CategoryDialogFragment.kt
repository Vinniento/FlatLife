package fh.wfp2.flatlife.ui.fragments.finance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import fh.wfp2.flatlife.R

class CategoryDialogFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var rootView: View =
            inflater.inflate(R.layout.add_expense_category_dialog, container, false)
        return rootView
    }
}