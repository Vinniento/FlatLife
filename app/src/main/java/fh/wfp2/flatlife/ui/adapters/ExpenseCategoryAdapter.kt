package fh.wfp2.flatlife.ui.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import fh.wfp2.flatlife.data.room.entities.ExpenseCategory
import fh.wfp2.flatlife.databinding.FinanceCategoryCardBinding
import timber.log.Timber

class ExpenseCategoryAdapter(val clickListener: (ExpenseCategory) -> Unit) :
    ListAdapter<ExpenseCategory, RecyclerView.ViewHolder>(ExpenseCategoryDiffCallback()) {

    var categoriesList = listOf<ExpenseCategory>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = categoriesList.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ExpenseCategoryItemViewHolder {

        val binding =
            FinanceCategoryCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        Timber.i("viewHolder created")
        return ExpenseCategoryItemViewHolder(binding)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = categoriesList[position]
        (holder as ExpenseCategoryItemViewHolder).bind(currentItem, clickListener)
    }


    inner class ExpenseCategoryItemViewHolder(private val binding: FinanceCategoryCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: ExpenseCategory, clickListener: (ExpenseCategory) -> Unit) {
            binding.apply {
                tvCategory.text = category.name
                tvCategory.setOnClickListener { clickListener(category) }
            }
        }

    }
}

private class ExpenseCategoryDiffCallback : DiffUtil.ItemCallback<ExpenseCategory>() {
    override fun areItemsTheSame(oldItem: ExpenseCategory, newItem: ExpenseCategory): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ExpenseCategory, newItem: ExpenseCategory): Boolean {
        return oldItem == newItem
    }

}

