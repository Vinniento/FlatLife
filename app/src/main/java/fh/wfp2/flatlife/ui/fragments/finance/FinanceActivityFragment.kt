package fh.wfp2.flatlife.ui.fragments.finance

import android.graphics.Canvas
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import fh.wfp2.flatlife.R
import fh.wfp2.flatlife.data.room.entities.FinanceActivity
import fh.wfp2.flatlife.databinding.FinanceActivityFragmentBinding
import fh.wfp2.flatlife.other.Status
import fh.wfp2.flatlife.ui.adapters.FinanceActivityAdapter
import fh.wfp2.flatlife.ui.fragments.BaseFragment
import fh.wfp2.flatlife.ui.viewmodels.finance.FinanceActivityViewModel
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class FinanceActivityFragment : BaseFragment(R.layout.finance_activity_fragment) {

    private val viewModel: FinanceActivityViewModel by viewModels()
    private lateinit var binding: FinanceActivityFragmentBinding
    private val financeAdapter =
        FinanceActivityAdapter { activity -> activityClicked(activity) }
    private val swipingItem = MutableLiveData(false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FinanceActivityFragmentBinding.bind(view)

        binding.apply {
            bActivity.isActivated = true
            financeActivityRecyclerview.apply {
                adapter = financeAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                ItemTouchHelper(SwipeToDelete(financeAdapter)).attachToRecyclerView(
                    this
                )
            }
        }

        subscribeToObservers()
        subscribeToFinanceEvents()
        setupOnClickListeners()
        setupSwipeRefreshLayout()
    }


    private fun setupOnClickListeners() {
        binding.apply {

            addActivity.setOnClickListener {
                viewModel.onAddActivityClick()
            }

            bBalance.setOnClickListener {
                viewModel.onBalanceButtonClick()
            }
        }
    }

    private fun subscribeToObservers() {
        viewModel.allItems.observe(viewLifecycleOwner, {
            it?.let { event ->
                val result = event.peekContent()
                when (result.status) {
                    Status.SUCCESS -> {
                        financeAdapter.activityList = result.data!!
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                    Status.ERROR -> {
                        event.getContentIfNotHandled()?.let { errorResource ->
                            errorResource.message?.let { message ->
                                showSnackbar(message)
                            }
                        }
                        result.data?.let { items ->
                            financeAdapter.activityList = items
                        }
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                    Status.LOADING -> {
                        result.data?.let { items ->
                            financeAdapter.activityList = items
                        }
                        binding.swipeRefreshLayout.isRefreshing = true
                    }
                }
            }
        })
    }

    private fun setupSwipeRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.syncAllItems()
        }
    }

    private fun subscribeToFinanceEvents() {
        binding.apply {
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.financeActivityEvents.collect { event ->
                    when (event) {
                        is FinanceActivityViewModel.FinanceActivityEvents.NavigateToAddExpenseCategoryScreen
                        -> {
                            val action =
                                FinanceActivityFragmentDirections.actionFinanceActivityFragment2ToFinanceCategoryFragment()
                            findNavController().navigate(action)
                        }
                        is FinanceActivityViewModel.FinanceActivityEvents.NavigateToAddExpenseActivityScreen -> {
                            val action =
                                FinanceActivityFragmentDirections.actionFinanceActivityFragment2ToAddExpenseFragment(
                                    financeActivity =
                                    event.financeActivity
                                )
                            findNavController().navigate(action)
                        }
                        is FinanceActivityViewModel.FinanceActivityEvents.NavigateToBalanceScreen -> {
                            val action =
                                FinanceActivityFragmentDirections.actionFinanceActivityFragment2ToFinanceBalanceFragment()
                            findNavController().navigate(action)
                        }
                        is FinanceActivityViewModel.FinanceActivityEvents.ShowUndoDeleteActivityMessage -> {
                            Snackbar.make(
                                requireView(),
                                "Activity deleted",
                                Snackbar.LENGTH_SHORT
                            )
                                .setAction("Undo") {
                                    viewModel.undoDeleteClick(event.item)
                                }
                                .show()
                        }
                    }
                }
            }
        }
    }

    private fun activityClicked(financeActivity: FinanceActivity) {
        viewModel.onActivityClicked(financeActivity)
    }

    inner class SwipeToDelete(var adapter: FinanceActivityAdapter) :
        ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition
            viewModel.onSwipedRight(adapter.activityList[position])
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                swipingItem.postValue(isCurrentlyActive)
            }
        }
    }
}