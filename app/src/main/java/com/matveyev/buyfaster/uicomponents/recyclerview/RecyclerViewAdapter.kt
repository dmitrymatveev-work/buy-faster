package com.matveyev.buyfaster.uicomponents.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.matveyev.buyfaster.BR

class RecyclerViewAdapter(private val items: List<*>,
                          private val layout: Int,
                          private val parentVM: Any?) :
    RecyclerView.Adapter<RecyclerViewAdapter.ItemViewHolder>() {

    class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun getItemViewType(position: Int): Int {
        return layout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(viewType, parent, false)

        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val binding: ViewDataBinding? = DataBindingUtil.bind(holder.view)
        binding?.run {
            setVariable(BR.viewModel, items[position])
            parentVM?.run {
                setVariable(BR.parentViewModel, parentVM)
            }
            executePendingBindings()
        }
    }

    override fun getItemCount() = items.size
}