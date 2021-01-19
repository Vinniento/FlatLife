package fh.wfp2.flatlife.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import fh.wfp2.flatlife.R
import fh.wfp2.flatlife.databinding.HomeFragmentBinding
import fh.wfp2.flatlife.ui.adapters.ShoppingAdapter
import fh.wfp2.flatlife.ui.adapters.TaskAdapter
import fh.wfp2.flatlife.ui.viewmodels.home.HomeViewModel
import fh.wfp2.flatlife.util.hideKeyboard
import timber.log.Timber


class HomeFragment : Fragment(R.layout.home_fragment) {

    private lateinit var binding: HomeFragmentBinding
    private lateinit var viewModel: HomeViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = HomeFragmentBinding.bind(view)
        hideKeyboard()
        val taskAdapter = TaskAdapter(null)
        val shoppingAdapter = ShoppingAdapter(null)
        viewModel =
            ViewModelProvider(this, defaultViewModelProviderFactory).get(HomeViewModel::class.java)
        binding.apply {

            rvTask.apply {
                adapter = taskAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
            rvShopping.apply {
                adapter = shoppingAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            viewModel.allShoppingItems.observe(viewLifecycleOwner, {
                it?.let {
                        shoppingAdapter.shoppingList = it.filter { item -> !item.isBought }
                }
            })
            viewModel.allTaskItems.observe(viewLifecycleOwner, {

                taskAdapter.taskList = it.filter { item -> !item.isComplete }
            })

        }
    }

    override fun onResume() {
        super.onResume()
        Timber.i("onResume called")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.i("onCreate called")
    }

    override fun onStart() {
        super.onStart()
        Timber.i("onStart called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.i("onDestroy called")
    }

    override fun onStop() {
        super.onStop()
        Timber.i("onStopCalled")
    }


}
