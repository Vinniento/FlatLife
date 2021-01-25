package fh.wfp2.flatlife.ui.home

import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import fh.wfp2.flatlife.R
import fh.wfp2.flatlife.databinding.HomeFragmentBinding
import fh.wfp2.flatlife.other.Constants
import fh.wfp2.flatlife.ui.adapters.ShoppingAdapter
import fh.wfp2.flatlife.ui.adapters.TaskAdapter
import fh.wfp2.flatlife.ui.fragments.BaseFragment
import fh.wfp2.flatlife.ui.viewmodels.home.HomeViewModel
import fh.wfp2.flatlife.util.hideKeyboard
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment(R.layout.home_fragment) {

    private lateinit var binding: HomeFragmentBinding
    private val viewModel: HomeViewModel by viewModels()

    @Inject
    lateinit var sharedPref: SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = HomeFragmentBinding.bind(view)
        hideKeyboard()
        val taskAdapter = TaskAdapter(null)
        val shoppingAdapter = ShoppingAdapter(null)

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

            /*viewModel.allShoppingItems.observe(viewLifecycleOwner, {
                it?.let {
                    shoppingAdapter.shoppingList = it.filter { item -> !item.isBought }
                }
            })*/
            viewModel.allTaskItems.observe(viewLifecycleOwner, {

                taskAdapter.taskList = it.filter { item -> !item.isComplete }
            })

        }
        setHasOptionsMenu(true)
        showBottomNavigation()
    }

    private fun logout() {
        sharedPref.edit().putString(Constants.KEY_LOGGED_IN_USERNAME, Constants.NO_USERNAME).apply()
        sharedPref.edit().putString(Constants.KEY_PASSWORD, Constants.NO_PASSWORD).apply()
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.homeFragment, true)
            .build()
        findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToAuthFragment(),
            navOptions
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_home_fragment, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
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
