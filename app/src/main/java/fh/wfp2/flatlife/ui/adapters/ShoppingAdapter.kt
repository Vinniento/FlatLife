package fh.wfp2.flatlife.ui.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import fh.wfp2.flatlife.R
import fh.wfp2.flatlife.data.room.entities.ShoppingItem
import fh.wfp2.flatlife.databinding.ShoppingItemCardBinding
import timber.log.Timber

class ShoppingAdapter(private val listener: OnItemClickListener<ShoppingItem>?) :
    ListAdapter<ShoppingItem, RecyclerView.ViewHolder>(ShoppingDiffCallback()) {

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
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = shoppingList[position]
        (holder as ShoppingItemViewHolder).bind(currentItem)
    }

    inner class ShoppingItemViewHolder(private val binding: ShoppingItemCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            //onClickListeners

            listener?.let {

                binding.apply {
                    root.setOnClickListener {
                        val position = adapterPosition
                        if (position != RecyclerView.NO_POSITION) {
                            val shoppingItem = shoppingList[position]
                            listener.onItemClick(shoppingItem)
                        }
                    }
                    cbItemBought.setOnClickListener {
                        val position = adapterPosition
                        if (position != RecyclerView.NO_POSITION) //-1 -> Wenn die view gerade ausm Sichtfeld kommt
                        {
                            val shoppingItem = shoppingList[position]
                            listener.onCheckBoxClick(shoppingItem, !shoppingItem.isBought)
                        }
                    }
                }
            }
        }

        fun bind(item: ShoppingItem) {
            binding.apply {
                tvItemName.text = item.name
                cbItemBought.isChecked = item.isBought

                if (!item.isSynced) {
                    ivSynced.setImageResource(R.drawable.ic_cross)
                } else {
                    ivSynced.setImageResource(R.drawable.ic_check)
                }
            }
        }
    }
}

private class ShoppingDiffCallback : DiffUtil.ItemCallback<ShoppingItem>() {
    override fun areItemsTheSame(oldItem: ShoppingItem, newItem: ShoppingItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ShoppingItem, newItem: ShoppingItem): Boolean {
        return oldItem == newItem
    }
}