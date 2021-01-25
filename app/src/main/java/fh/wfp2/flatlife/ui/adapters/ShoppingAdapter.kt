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

class ShoppingAdapter :
    ListAdapter<ShoppingItem, RecyclerView.ViewHolder>(ShoppingDiffCallback()) {

    var shoppingList = listOf<ShoppingItem>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private var onCheckboxClickListener: ((ShoppingItem) -> Unit)? = null
    private var onItemClickListener: ((ShoppingItem) -> Unit)? = null

    override fun getItemCount(): Int = shoppingList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingItemViewHolder {

        val binding =
            ShoppingItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        Timber.i("viewHolder created")
        return ShoppingItemViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val shoppingItem = shoppingList[position]

        holder.itemView.setOnClickListener {

        }
        (holder as ShoppingItemViewHolder).bind(shoppingItem)
    }

    fun setOnCheckboxClickListener(onCheckBoxClick: (ShoppingItem) -> Unit) {
        this.onCheckboxClickListener = onCheckBoxClick
    }

    fun setOnItemClickListener(onItemClick: (ShoppingItem) -> Unit) {
        this.onItemClickListener = onItemClick
    }

    inner class ShoppingItemViewHolder(private val binding: ShoppingItemCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {

            //onClickListeners
            binding.apply {
                constraintLayout.setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        val shoppingItem = shoppingList[adapterPosition]
                        onItemClickListener?.let {
                            it(shoppingItem)
                        }
                    }
                }

                cbItemBought.setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION) //-1 -> Wenn die view gerade ausm Sichtfeld kommt
                    {
                        val shoppingItem = shoppingList[adapterPosition]
                        onCheckboxClickListener?.let {
                            it(shoppingItem.apply { isBought = !isBought })
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