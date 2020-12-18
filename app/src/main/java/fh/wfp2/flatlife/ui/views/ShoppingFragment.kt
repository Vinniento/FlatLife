package fh.wfp2.flatlife.ui.views

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import fh.wfp2.flatlife.R
import fh.wfp2.flatlife.databinding.ShoppingFragmentBinding
import fh.wfp2.flatlife.ui.adapters.ShoppingAdapter
import fh.wfp2.flatlife.ui.viewmodels.ShoppingViewModel
import fh.wfp2.flatlife.ui.viewmodels.ShoppingViewModelFactory
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

        //onClickListener here


        //Recyclerview
        val adapter = ShoppingAdapter()
        val recyclerView = binding.shoppingListRecyclerview
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        setHasOptionsMenu(true)

        //binding.taskViewModel = viewModel
        Timber.i("ViewModel created and added to binding")
        binding.apply {
            shoppingListRecyclerview.layoutManager = LinearLayoutManager(context)
        }

        //observers
        viewModel.allItems.observe(viewLifecycleOwner, Observer {

            adapter.shoppingList = viewModel.allItems.value!!
        })

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
