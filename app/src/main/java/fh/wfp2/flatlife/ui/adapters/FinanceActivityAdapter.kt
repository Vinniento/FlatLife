package fh.wfp2.flatlife.ui.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import fh.wfp2.flatlife.data.room.entities.FinanceActivity
import fh.wfp2.flatlife.data.room.entities.mapping.ExpenseCategoryWithFinanceActivity
import fh.wfp2.flatlife.databinding.FinanceActivityCardBinding
import timber.log.Timber

class FinanceActivityAdapter(val clickListener: (FinanceActivity) -> Unit) :
    ListAdapter<FinanceActivity, RecyclerView.ViewHolder>(FinanceActivityDiffCallback()) {

    var activityList = listOf<FinanceActivity>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = activityList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FinanceActivityViewHolder {
        Timber.i("viewHolder created")
        val binding =
            FinanceActivityCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FinanceActivityViewHolder(binding)
    }

    //gets called each time a view comes into view
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = activityList[position]
        (holder as FinanceActivityViewHolder).bind(currentItem, clickListener)
    }

    inner class FinanceActivityViewHolder(private val binding: FinanceActivityCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(financeActivity: FinanceActivity, clickListener: (FinanceActivity) -> Unit) {
            binding.apply {
                tvAmount.text = financeActivity.price.toString()
                tvDescription.text = financeActivity.description
                tvDate.text = financeActivity.createdDateFormatted
                root.setOnClickListener { clickListener(financeActivity) }
            }
        }
    }
}

private class FinanceActivityDiffCallback : DiffUtil.ItemCallback<FinanceActivity>() {
    override fun areItemsTheSame(oldItem: FinanceActivity, newItem: FinanceActivity): Boolean {
        return oldItem.activityId == newItem.activityId
    }

    override fun areContentsTheSame(oldItem: FinanceActivity, newItem: FinanceActivity): Boolean {
        return oldItem == newItem
    }
}