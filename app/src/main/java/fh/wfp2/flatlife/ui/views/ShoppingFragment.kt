package fh.wfp2.flatlife.ui.views

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import fh.wfp2.flatlife.R
import fh.wfp2.flatlife.databinding.ShoppingFragmentBinding
import fh.wfp2.flatlife.ui.adapters.ShoppingAdapter
import fh.wfp2.flatlife.ui.viewmodels.ShoppingViewModel
import fh.wfp2.flatlife.ui.viewmodels.ShoppingViewModelFactory
import kotlinx.coroutines.flow.collect
import timber.log.Timber


class ShoppingFragment : Fragment(R.layout.shopping_fragment) {

    private lateinit var viewModel: ShoppingViewModel
    private lateinit var viewModelFactory: ShoppingViewModelFactory
    private lateinit var binding: ShoppingFragmentBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ShoppingFragmentBinding.bind(view)

        val application = requireNotNull(this.activity).application
        viewModelFactory = ShoppingViewModelFactory(application)

        viewModel = ViewModelProvider(this, viewModelFactory).get(ShoppingViewModel::class.java)

        //Recyclerview
        val shoppingAdapter = ShoppingAdapter()

        //binding.taskViewModel = viewModel
        Timber.i("ViewModel created and recyclerview added to binding")
        binding.apply {
            shoppingListRecyclerview.apply {

                adapter = shoppingAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)

            }
            shoppingListRecyclerview.layoutManager = LinearLayoutManager(context)

            //onclickLIsteners
            addShoppingItem.setOnClickListener {
                viewModel.onAddItemClick(binding.etShoppingItemInput.text.toString())
            }
        }


        //observers\
        viewModel.allItems.observe(viewLifecycleOwner, {
            it?.let {
                //shoppingAdapter.submitList(it)
                shoppingAdapter.shoppingList = it
                it.forEach { item ->
                    Timber.i("\nItem: ${item.name}")
                }
            }
        })


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


                }
            }
        }
        setHasOptionsMenu(true)

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
