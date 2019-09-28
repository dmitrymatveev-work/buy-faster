package com.matveyev.buyfaster.uicomponents.recyclerview

import androidx.databinding.ObservableList
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

val touchHelpers = mutableListOf<Pair<ObservableList<*>, ItemTouchHelper>>()

fun <T> RecyclerView.setItemsSource(
    oldItems: ObservableList<T>?,
    oldLayout: Int,
    newItems: ObservableList<T>,
    newLayout: Int,
    swipe: Boolean,
    parentVM: Any?) {

    for (i in touchHelpers.size - 1 downTo 0) {
        val pair = touchHelpers[i]
        if(pair.first.size == 0){
            pair.second.attachToRecyclerView(null)
            touchHelpers.removeAt(i)
        }
    }

    if(newItems.size == 0){
        return
    }

    if(newItems == oldItems){
        for (pair in touchHelpers){
            if(pair.first == newItems){
                return
            }
        }
    }

    val viewAdapter = RecyclerViewAdapter(newItems, newLayout, parentVM)

    val callback = object: ObservableList.OnListChangedCallback<ObservableList<T>>() {
        override fun onChanged(sender: ObservableList<T>?) {
            viewAdapter.notifyDataSetChanged()
        }

        override fun onItemRangeChanged(sender: ObservableList<T>?, positionStart: Int, itemCount: Int) {
            viewAdapter.notifyItemRangeChanged(positionStart, itemCount)
        }

        override fun onItemRangeInserted(sender: ObservableList<T>?, positionStart: Int, itemCount: Int) {
            viewAdapter.notifyItemRangeInserted(positionStart, itemCount)
        }

        override fun onItemRangeMoved(
            sender: ObservableList<T>?,
            fromPosition: Int,
            toPosition: Int,
            itemCount: Int
        ) {
            for(i in 0..(itemCount-1)){
                viewAdapter.notifyItemMoved(fromPosition+i, toPosition+i)
            }
        }

        override fun onItemRangeRemoved(sender: ObservableList<T>?, positionStart: Int, itemCount: Int) {
            viewAdapter.notifyItemRangeRemoved(positionStart, itemCount)
        }
    }

    val swipeDirs = if (swipe) ItemTouchHelper.LEFT else 0

    val touchHelper = ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP or ItemTouchHelper.DOWN, swipeDirs){
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val fromPos = viewHolder.adapterPosition
            val toPos = target.adapterPosition

            viewAdapter.notifyItemMoved(fromPos, toPos)

            newItems.removeOnListChangedCallback(callback)
            val tmp = newItems[fromPos]
            val delta = if (fromPos < toPos) 1 else -1
            var i = fromPos
            while (i != toPos){
                newItems[i] = newItems[i+delta]
                i += delta
            }
            newItems[toPos] = tmp
            newItems.addOnListChangedCallback(callback)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            newItems.removeAt(viewHolder.adapterPosition)
        }
    })

    touchHelpers.add(Pair(newItems, touchHelper))

    if(this.layoutManager == null){
        this.layoutManager = LinearLayoutManager(this.context)
    }
    this.adapter = viewAdapter

    newItems.addOnListChangedCallback(callback)

    touchHelper.attachToRecyclerView(this)
}