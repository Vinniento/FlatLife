package fh.wfp2.flatlife.ui.home

import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import dagger.hilt.android.AndroidEntryPoint
import fh.wfp2.flatlife.R
import fh.wfp2.flatlife.databinding.HomeFragmentBinding
import fh.wfp2.flatlife.other.Constants
import fh.wfp2.flatlife.ui.adapters.ShoppingAdapter
import fh.wfp2.flatlife.ui.adapters.TaskAdapter
import fh.wfp2.flatlife.ui.fragments.BaseFragment
import fh.wfp2.flatlife.ui.viewmodels.home.HomeViewModel
import fh.wfp2.flatlife.ui.viewmodels.shopping.ShoppingViewModel
import fh.wfp2.flatlife.ui.viewmodels.tasks.TaskViewModel
import fh.wfp2.flatlife.util.hideKeyboard
import kotlinx.android.synthetic.main.add_chore_dialog.b_cancel
import kotlinx.android.synthetic.main.add_chore_dialog.sp_assign_to
import kotlinx.android.synthetic.main.add_user_to_flat_dialog.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import timber.log.Timber
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class HomeFragment : BaseFragment(R.layout.home_fragment) {

    private lateinit var binding: HomeFragmentBinding
    private val viewModel: HomeViewModel by viewModels()
    private val shoppingViewModel: ShoppingViewModel by viewModels()
    private val taskViewModel: TaskViewModel by viewModels()
    private lateinit var usersSpinner: Spinner

    @Inject
    lateinit var sharedPref: SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = HomeFragmentBinding.bind(view)
        hideKeyboard()
        val taskAdapter = TaskAdapter(null)
        val shoppingAdapter = ShoppingAdapter()

        binding.apply {

            rvTask.apply {
                adapter = taskAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                taskAdapter.setOnCheckBoxListener {
                    taskViewModel.onTaskCheckChanged(it)
                }

            }
            rvShopping.apply {
                adapter = shoppingAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                shoppingAdapter.setOnCheckboxClickListener {
                    shoppingViewModel.onShoppingItemCheckedChanged(it)
                }
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
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_home_fragment, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                logout()
                true
            }
            R.id.action_add_user_to_flat -> {
                showAddChoreDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showAddChoreDialog() {
        val dialog = MaterialDialog(requireContext())
            .noAutoDismiss()
            .customView(R.layout.add_user_to_flat_dialog)

        val users = listOf("Vince", "Frank", "Berna")

        usersSpinner = dialog.sp_assign_to
        usersSpinner.adapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                users
            )

        dialog.b_addUser.setOnClickListener {
            val userToBeAdded = usersSpinner.selectedItem.toString()

            viewModel.addUserToFlat(userToBeAdded)
            Timber.i("$userToBeAdded added to flat")
            dialog.dismiss()
        }
        dialog.b_cancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
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
