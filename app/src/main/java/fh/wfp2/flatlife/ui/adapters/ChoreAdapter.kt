package fh.wfp2.flatlife.ui.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import fh.wfp2.flatlife.data.room.entities.Chore
import fh.wfp2.flatlife.databinding.ChoreItemCardBinding
import timber.log.Timber


class ChoreAdapter(private val listener: OnItemClickListener<Chore>) :
    ListAdapter<Chore, RecyclerView.ViewHolder>(ChoreDiffCallback()) {

    var choreList = listOf<Chore>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    override fun getItemCount(): Int = choreList.size


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChoreViewHolder {
        Timber.i("viewHolder created")
        val binding =
            ChoreItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChoreViewHolder(binding)
    }

    //gets called each time a view comes into view
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = choreList[position]
        (holder as ChoreViewHolder).bind(currentItem)
    }

    //only gets called when viewHolder gets first created (ViewHolder get reused!!)
    inner class ChoreViewHolder(private val binding: ChoreItemCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(choreList[position])
                    }
                }
                cbChoreCompleted.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val chore = choreList[position]
                        listener.onCheckBoxClick(chore, !chore.isComplete)
                    }
                }
            }

        }

        fun bind(chore: Chore) {
            binding.apply {
                tvChoreName.text = chore.name
                tvDueBy.text = chore.dueBy.toString()
                tvUsername.text = chore.assignedTo
                cbChoreCompleted.isChecked = chore.isComplete

            }

        }
    }
}

private class ChoreDiffCallback : DiffUtil.ItemCallback<Chore>() {
    override fun areItemsTheSame(oldItem: Chore, newItem: Chore): Boolean {
        return oldItem.choreId == newItem.choreId
    }

    override fun areContentsTheSame(oldItem: Chore, newItem: Chore): Boolean {
        return oldItem == newItem
    }
}