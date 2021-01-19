package fh.wfp2.flatlife.ui.fragments.shopping

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import fh.wfp2.flatlife.R
import fh.wfp2.flatlife.databinding.EditShoppingItemFragmentBinding
import fh.wfp2.flatlife.ui.viewmodels.shopping.EditShoppingItemFragmentViewModel
import fh.wfp2.flatlife.util.hideKeyboard
import kotlinx.coroutines.flow.collect
import timber.log.Timber

class EditShoppingItem : Fragment(R.layout.edit_shopping_item_fragment) {

    private lateinit var binding: EditShoppingItemFragmentBinding

    private val args: EditShoppingItemArgs by navArgs()
    private val viewModel: EditShoppingItemFragmentViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = EditShoppingItemFragmentBinding.bind(view)

        args.shoppingItem?.let {
            viewModel.onArgumentsPassed(it)
        }

        viewModel.item.observe(viewLifecycleOwner, {
            binding.apply {
                it?.let {
                    etShoppingItem.setText(viewModel.item.value?.name)
                    cbIsBought.isChecked = viewModel.item.value?.isBought ?: false
                    // cbImportant.jumpDrawablesToCurrentState() =
                }
            }
        })

        binding.bUpdateItem.setOnClickListener {

            viewModel.onUpdateItemClick(
                binding.etShoppingItem.text.toString(),
                binding.cbIsBought.isChecked
            )
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.editShoppingItemEvent.collect { event ->

                when (event) {
                    is EditShoppingItemFragmentViewModel.EditShoppingItemEvent.ShowIncompleteShoppingItemMessage -> {
                        Snackbar.make(
                            requireView(),
                            "The task field can't be empty",
                            Snackbar.LENGTH_SHORT
                        ).show()
                        Timber.i("Snackbar shown")
                    }
                    is EditShoppingItemFragmentViewModel.EditShoppingItemEvent.NavigateToShoppingFragmentScreen -> {
                        val action =
                            EditShoppingItemDirections.actionEditShoppingItemToShoppingFragment()
                        findNavController().navigate(action)
                    }
                }
            }
        }

    }
}