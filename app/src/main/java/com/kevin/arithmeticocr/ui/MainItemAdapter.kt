package com.kevin.arithmeticocr.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kevin.arithmeticocr.data.EquationResult
import com.kevin.arithmeticocr.databinding.ItemResultBinding

class MainItemAdapter: RecyclerView.Adapter<MainItemAdapter.ViewHolder>() {

    private val mItems: MutableList<EquationResult> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemResultBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mItems[position])
    }

    override fun getItemCount(): Int = mItems.size

    inner class ViewHolder(private val binding: ItemResultBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: EquationResult) {
            binding.tvEquation.text = "Input: ${data.equation}"
            binding.tvResult.text = "Result: ${data.result}"
        }
    }

    fun setItems(data: List<EquationResult>) {
        mItems.clear()
        mItems.addAll(data)

        notifyDataSetChanged()
    }

}