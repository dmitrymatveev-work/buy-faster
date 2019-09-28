package com.matveyev.buyfaster.uicomponents.recyclerview

import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableList
import androidx.recyclerview.widget.RecyclerView
import com.matveyev.buyfaster.vms.SubjectVM

@BindingAdapter("itemsSource", "itemsLayout", "parentVM")
fun RecyclerView.setItemsSource(
    oldItems: ObservableList<SubjectVM>?,
    oldLayout: Int,
    oldParentVM: Any?,
    newItems: ObservableList<SubjectVM>,
    newLayout: Int,
    newParentVM: Any) {
    this.setItemsSource<SubjectVM>(oldItems, oldLayout, newItems, newLayout, true, newParentVM)
}