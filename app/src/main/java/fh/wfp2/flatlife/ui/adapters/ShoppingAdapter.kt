package fh.wfp2.flatlife.ui.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import fh.wfp2.flatlife.data.room.ShoppingItem
import fh.wfp2.flatlife.databinding.ShoppingItemCardBinding
import timber.log.Timber

class ShoppingAdapter :
    RecyclerView.Adapter<ShoppingAdapter.ShoppingItemViewHolder>() {

    var shoppingList = listOf<ShoppingItem>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = shoppingList.size


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingItemViewHolder {

        val binding =
            ShoppingItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        Timber.i("viewHolder created")
        return ShoppingItemViewHolder(binding)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ShoppingItemViewHolder, position: Int) {
        val currentItem = shoppingList[position]
        holder.bind(currentItem)
    }


    inner class ShoppingItemViewHolder(private val binding: ShoppingItemCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            //onClickListeners
        }

        fun bind(shoppingItem: ShoppingItem) {
            binding.apply {
                tvItemName.text = shoppingItem.name
                cbItemBought.isChecked = shoppingItem.isBought
            }
        }

    }
}
