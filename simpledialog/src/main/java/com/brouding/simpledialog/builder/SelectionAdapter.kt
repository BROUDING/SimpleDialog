package com.brouding.simpledialog.builder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.brouding.simpledialog.databinding.BroudingSimpleDialogSelectionRowBinding

class SelectionAdapter(
    private val onSelectListener: OnSelectListener,
    private val paddingLeftDp: Int? = 14
): ListAdapter<String, RecyclerView.ViewHolder>(OptionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val listItemSelectionBinding = BroudingSimpleDialogSelectionRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SelectionViewHolder(listItemSelectionBinding, onSelectListener, paddingLeftDp)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val option = getItem(position)
        (holder as SelectionViewHolder).bind(option)
    }

    class SelectionViewHolder(
        private val binding: BroudingSimpleDialogSelectionRowBinding,
        private val onSelectListener: OnSelectListener,
        private val paddingLeftDp: Int? = 14
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            binding.apply {
                option = item

                option?.let { option ->
                    binding.root.apply {
                        paddingLeftDp?.let {
                            setPadding(it, this.paddingTop, this.paddingRight, this.paddingBottom)
                        }
                        setOnClickListener {
                            onSelectListener.onSelect(option)
                        }
                    }
                }
            }
        }
    }

    interface OnSelectListener {
        fun onSelect(option: String)
    }
}

private class OptionDiffCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}