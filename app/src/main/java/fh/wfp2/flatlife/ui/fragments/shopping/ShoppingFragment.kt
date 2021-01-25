package fh.wfp2.flatlife.ui.fragments.shopping

import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
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
import fh.wfp2.flatlife.databinding.ShoppingFragmentBinding
import fh.wfp2.flatlife.other.Status
import fh.wfp2.flatlife.ui.adapters.ShoppingAdapter
import fh.wfp2.flatlife.ui.fragments.BaseFragment
import fh.wfp2.flatlife.ui.viewmodels.shopping.ShoppingViewModel
import fh.wfp2.flatlife.util.hideKeyboard
import kotlinx.coroutines.flow.collect
import timber.log.Timber

@AndroidEntryPoint
class ShoppingFragment : BaseFragment(R.layout.shopping_fragment) {

    private val viewModel: ShoppingViewModel by viewModels()
    private lateinit var binding: ShoppingFragmentBinding

    //Recyclerview
    private val shoppingAdapter = ShoppingAdapter()

    private val swipingItem = MutableLiveData(false)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideKeyboard()
        binding = ShoppingFragmentBinding.bind(view)
        setupRecyclerviewAdapter()
        setupSwipeRefreshLayout()
        setupOnClickListeners()
        setupEventListeners()
        subscribeToObservers()
        Timber.i("ViewModel created and recyclerview added to binding")

        setHasOptionsMenu(true)
    }

    private fun subscribeToObservers() {
        viewModel.allItems.observe(viewLifecycleOwner, {
            it?.let { event ->
                val result = event.peekContent()
                when (result.status) {
                    Status.SUCCESS -> {
                        shoppingAdapter.shoppingList = result.data!!
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                    Status.ERROR -> {
                        event.getContentIfNotHandled()?.let { errorResource ->
                            errorResource.message?.let { message ->
                                showSnackbar(message)
                            }
                        }
                        result.data?.let { items ->
                            shoppingAdapter.shoppingList = items
                        }
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                    Status.LOADING -> {
                        result.data?.let { items ->
                            shoppingAdapter.shoppingList = items
                        }
                        binding.swipeRefreshLayout.isRefreshing = true
                    }
                }
            }
        })
        swipingItem.observe(viewLifecycleOwner, {
            binding.swipeRefreshLayout.isEnabled = !it
        })

    }

    private fun setupRecyclerviewAdapter() {
        binding.apply {
            shoppingListRecyclerview.apply {
                adapter = shoppingAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)

                ItemTouchHelper(SwipeToDelete(shoppingAdapter)).attachToRecyclerView(
                    shoppingListRecyclerview
                )
            }
            shoppingAdapter.setOnCheckboxClickListener {
                viewModel.onShoppingItemCheckedChanged(it)
            }
            shoppingAdapter.setOnItemClickListener {
                viewModel.onShoppingItemSelected(it)
            }
        }
    }

    private fun setupEventListeners() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addShoppingItemEvents.collect { event ->
                when (event) {
                    is ShoppingViewModel.ShoppingEvents.ShowIncompleteItemMessage -> {
                        Snackbar.make(requireView(), "Item name is empty :(", Snackbar.LENGTH_SHORT)
                            .show()
                    }
                    is ShoppingViewModel.ShoppingEvents.ShowItemAddedMessage -> {
                        Snackbar.make(requireView(), "${event.item} added", Snackbar.LENGTH_SHORT)
                            .show()
                    }
                    is ShoppingViewModel.ShoppingEvents.ShowUndoDeleteTaskMessage -> {
                        Snackbar.make(
                            requireView(),
                            "${event.item.name} deleted",
                            Snackbar.LENGTH_SHORT
                        )
                            .setAction("Undo deleting ${event.item.name}") {
                                viewModel.undoDeleteClick(event.item)
                            }
                            .show()
                    }
                    is ShoppingViewModel.ShoppingEvents.NavigateToEditShoppingItemFragment -> {
                        val action =
                            ShoppingFragmentDirections.actionShoppingFragmentToEditShoppingItem(
                                event.item
                            )
                        findNavController().navigate(action)
                    }
                }
            }
        }
    }

    private fun setupOnClickListeners() {
        //onclickLIsteners
        binding.apply {
            addShoppingItem.setOnClickListener {
                viewModel.onAddItemClick(binding.etShoppingItemInput.text.toString())
                etShoppingItemInput.setText("")
            }
        }
    }

    private fun setupSwipeRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.syncAllTasks()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_shopping, menu)
        //super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete_all_bought_items -> {
                viewModel.deleteAllBoughtItems()
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

    inner class SwipeToDelete(var adapter: ShoppingAdapter) :
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
            viewModel.onSwipedRight(adapter.shoppingList[position])
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