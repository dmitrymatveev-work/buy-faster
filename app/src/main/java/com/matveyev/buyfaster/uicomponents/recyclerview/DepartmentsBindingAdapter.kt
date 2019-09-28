package com.matveyev.buyfaster.uicomponents.recyclerview

import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableList
import androidx.recyclerview.widget.RecyclerView
import com.matveyev.buyfaster.vms.DepartmentVM

@BindingAdapter("itemsSource", "itemsLayout", "parentVM")
fun RecyclerView.setItemsSource(
    oldItems: ObservableList<DepartmentVM>?,
    oldLayout: Int,
    oldParentVM: Any?,
    newItems: ObservableList<DepartmentVM>,
    newLayout: Int,
    newParentVM: Any) {
    this.setItemsSource<DepartmentVM>(oldItems, oldLayout, newItems, newLayout, false, newParentVM)
}